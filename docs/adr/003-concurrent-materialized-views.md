# ADR 003: Concurrent Materialized Views for Analytical Dashboards

## Context
The ReconX Grafana dashboard requires a "Today At A Glance" widget to display aggregate metrics (Total Trades, Gross Notional, Match Rate). Computing this dynamically against a 50M row `trades` table requires multiple complex `SUM` and `FILTER` clauses, resulting in latency >600ms per dashboard reload.

## Problem
Standard PostgreSQL `MATERIALIZED VIEW` objects block read access (`ACCESS EXCLUSIVE` lock) entirely while being refreshed. If a cron job refreshes the view every 5 minutes, the dashboard will randomly hang or throw lock wait timeouts for users during that window.

## Alternatives
1. **Real-time Aggregation:** Run queries against the live partitioned tables. Incurs high CPU penalty under dashboard load.
2. **Standard Materialized View:** Locks the view during refresh. Unacceptable UX.
3. **Concurrent Materialized View (Selected):** Allows read access to stale data while the background refresh calculates the delta.

## Decision
We will use a Materialized View for analytical rollups and refresh it using `REFRESH MATERIALIZED VIEW CONCURRENTLY`.

## Consequences
- **Positive:** Read latency for the dashboard drops to <5ms.
- **Positive:** Dashboards never hang during the background cron refresh cycle.
- **Negative:** Requires a `UNIQUE INDEX` covering the grouped dimensions (`trade_date`, `region`, `asset_class`), slightly increasing storage overhead.

## AI Prompt
*“How do we optimize PostgreSQL for a Grafana dashboard doing heavy aggregates over 50M rows, ensuring that cache refreshes do not block read operations for active users?”*
