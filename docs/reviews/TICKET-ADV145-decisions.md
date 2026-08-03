# TICKET-ADV145 — Kafka Consumer Config Review

## Review Prompt

See [TICKET-ADV145-prompt.md](./TICKET-ADV145-prompt.md) for the full prompt sent to the AI reviewer.

---

## AI Review Findings & Team Decisions

The following table lists every finding returned from the config review, covering all five required areas (backpressure, error handling, idempotence, observability, security), along with the team's decision and rationale.

| # | Area | Finding | Config Key | Recommended Value | Decision | Rationale |
|---|------|---------|------------|-------------------|----------|-----------|
| 1 | Backpressure & Poll Tuning | Default `max.poll.records` is 500. At 500 events/sec, a slow `AuditEventConsumer` or `ReconciliationConsumer` risks exceeding `max.poll.interval.ms` (default 5 min), causing a spurious rebalance and reprocessing storm. | `spring.kafka.consumer.properties.max.poll.records` | `100` | **Accept** | Slow downstream JPA writes (audit_log, recon engine) justify a tighter poll batch; prevents rebalance storms under load spikes |
| 2 | Error Handling | `ExponentialBackOff` has no jitter. Under a thundering-herd failure (all 3 consumer groups retrying simultaneously), retries fire in lock-step, amplifying downstream pressure at exactly 1s, 2s, 4s intervals. | Custom `BackOff` implementation with jitter | Add random ±20% jitter to each interval | **Defer** | Valid production concern, but implementing a custom `BackOff` subclass is out of scope for Day 9; logged as backlog item for hardening sprint |
| 3 | Idempotence | Producer `enable.idempotence` is not set. At 500 events/sec, broker leader failovers can cause duplicate sends; with strict audit requirements, a duplicated `TRADE_CREATED` event would create a phantom row in `audit_log`. | `spring.kafka.producer.properties.enable.idempotence` | `true` | **Accept** | Cheap insurance with zero throughput cost; mandatory for a system with strict audit requirements |
| 4 | Observability | `metric.reporters` is set on the consumer only. The producer client has no Micrometer reporter wired, meaning `kafka_producer_record_send_total` (used by the ADV141 Grafana panel) may not surface in Prometheus. | `spring.kafka.producer.properties.metric.reporters` | `io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics` | **Accept** | Required to make the ADV141 Throughput panel's "produced" series non-empty; symmetric with the consumer-side config already in place |
| 5 | Security | `bootstrap-servers` resolves to `localhost:9092` (PLAINTEXT) by default. In production, all Kafka traffic would be unencrypted and unauthenticated, violating the audit and compliance requirements of the reconciliation service. | `spring.kafka.properties.security.protocol` | `SASL_SSL` (prod profile only) | **Reject** | Known dev-environment gap; switching to `SASL_SSL` requires broker certificates and JAAS config that are out of scope for the local Docker setup. Tracked separately for Day 10 infrastructure hardening. |

---

## Accepted Changes Applied

### Finding #1 — `max.poll.records: 100`
Added to `application.yml` under `spring.kafka.consumer.properties`.

### Finding #3 — `enable.idempotence: true`
Added to `application.yml` under `spring.kafka.producer.properties`.

### Finding #4 — Producer `metric.reporters`
Added `metric.reporters: io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics` under `spring.kafka.producer.properties` to mirror the consumer-side config.

---

## Deferred Items

- **Finding #2** (jitter on ExponentialBackOff): Filed as a backlog item. A custom `BackOff` implementation adding ±20% jitter to each interval is the recommended fix when the hardening sprint begins.

## Rejected Items

- **Finding #5** (SASL_SSL): The local Docker Compose broker runs in PLAINTEXT mode. Switching to SASL_SSL requires broker-side certificates, a JAAS config file, and Spring profile separation — all scoped for Day 10's infrastructure track, not Day 9 application code.
