# ReconX — Team Retrospective

> Deutsche Bank — TDI 2026 Graduate Technical Training Programme (Advanced Track)

---

## What worked?
- **Pair Programming on Kafka & Liquibase**: Pairing on Day 1 (Liquibase master changelogs) and Day 9 (Kafka multi-topic configuration) eliminated integration friction early.
- **Automated Healthchecks & Smoke Script**: The 7-step `scripts/smoke-test.sh` script made environment validation fast and deterministic across dev environments.
- **Multi-Stage Docker Layer Caching**: Separating dependency download (`dependency:go-offline`) from source compilation kept second-pass image builds under 25 seconds.
- **Auto-Provisioned Grafana Dashboards**: Mounting datasource and dashboard YAML files allowed instant observability on first boot without manual UI configuration.

---

## What didn't work?
- **Initial Context Path Mismatch**: We lost 45 minutes on Day 6 debugging why Prometheus couldn't scrape `/actuator/prometheus` because the Spring Boot servlet context-path was set to `/api`.
- **Stale `package-lock.json` in Frontend**: A minor dependency update caused initial Docker builds to fail until we aligned `npm install` flags in the multi-stage build.
- **Kafka Listener Initialization Delay**: Kafka consumer readiness took longer than Postgres on cold boots, requiring tuned `start_period` and retries in healthcheck definitions.

---

## What would you change?
- **Lock Data Model & Schemas on Day 1**: Finalize all core entity structures early to avoid retrofitting DTO mappers and Liquibase changesets later in the program.
- **Integrate Dockerfiles Earlier**: Write baseline Dockerfiles by Day 7 rather than consolidating containerization entirely on Day 10.
- **Add Automated Frontend E2E Smoke Tests**: Supplement the shell smoke script with a lightweight Playwright or Cypress E2E check.

---

## What surprised you?
- **Impact of Java 25 Virtual Threads**: High concurrency request processing scale-up required minimal thread tuning compared to traditional thread pools.
- **Value of Micrometer Custom Metrics**: Exposing business metrics (`recon_break_count`, `trades_by_status`) provided immediate visibility into pipeline health.
- **Strictness of JaCoCo Coverage Gates**: Enforcing 85% line coverage automatically caught several unhandled exception branches in custom REST controllers.

---

## Technical notes for the next cohort
1. **Always set `classpath:` prefix in Liquibase**: When wiring Liquibase in Spring Boot, specify `classpath:db/changelog/db.changelog-master.xml`; omitting `classpath:` causes silent skips in packaged JARs.
2. **Docker Compose Service DNS**: Never use `localhost` inside container-to-container communications; use Docker network service names (e.g., `http://backend:8080` or `kafka:29092`).
3. **Kafka Advertised Listeners**: Always configure dual listeners (`PLAINTEXT://kafka:29092` for internal docker containers and `PLAINTEXT_HOST://localhost:9092` for host tools).

---

## Team Roster

| Name | Role | Core Focus & Deliverables |
|---|---|---|
| **Abhikrit** | Team Lead / Full-Stack | Architecture, System Integration, React UI, Project Governance |
| **DevOps Engineer** | DevOps & CI/CD | Docker multi-stage builds, Compose stack, GitHub Actions, GHCR |
| **Backend Engineer** | Backend & Data | Java 25 Spring Boot services, Liquibase migrations, Envers audit |
| **Streaming Engineer** | Event Streaming | Kafka producers, consumers, DLQ pattern, Micrometer metrics |
