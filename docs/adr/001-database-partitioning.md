# ADR 001: Database Partitioning for Trades Table

## Context
The `trades` table in ReconX acts as the system of record for all executed transactions globally. Projections estimate ingestion rates exceeding 50 million rows per year. Analytical and EOD reconciliation queries will typically filter by a rolling monthly window, scanning massive volumes of historical data that are largely immutable after settlement.

## Problem
A single monolithic `trades` table will suffer from severe index bloat and degraded query performance due to sequential scanning. Deleting obsolete historical trades (purging) via standard `DELETE` statements will cause substantial write-ahead log (WAL) pressure and table bloat.

## Alternatives
1. **Single Table with B-Tree Indexes:** Simple, but write throughput decreases logarithmically as indexes grow. Purging is expensive.
2. **PostgreSQL RANGE Partitioning (Selected):** Partition the table declaratively by `trade_date`.
3. **Application-Level Sharding:** Route data to `trades_2026_01` etc. natively from the Spring Boot application. High complexity in JPA.

## Decision
We will implement declarative **RANGE Partitioning on `trade_date`** within PostgreSQL 16. The database will manage a rolling 12-month active partition window, with a `DEFAULT` partition to gracefully catch outliers (e.g. late arrivals from upstream Kafka feeds).

## Consequences
- **Positive:** Query planner uses "Partition Pruning" to skip unqueried months, reducing I/O drastically.
- **Positive:** Data purging becomes an `O(1)` metadata operation (`DROP TABLE trades_legacy`) rather than a slow row-level `DELETE`.
- **Negative:** Primary and Unique keys must include the partition key (`trade_date`), which slightly complicates JPA `@Id` mapping, requiring composite keys or UUIDs as a logical ID.

## AI Prompt
*“Design a database schema strategy to handle 50M trades/year in PostgreSQL, focusing on query speed for EOD reconciliation over monthly intervals and efficient data purging.”*
