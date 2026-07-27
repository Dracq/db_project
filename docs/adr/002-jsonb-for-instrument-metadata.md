# ADR 002: JSONB for Instrument Metadata

## Context
Financial instruments possess a wide variety of attributes that differ significantly across asset classes. An Equity might have `dividend_yield`, while a Fixed Income instrument might require `coupon_rate` and `maturity_date`. These attributes frequently evolve as new asset classes are onboarded.

## Problem
Creating individual SQL columns for every possible attribute leads to a sparsely populated (wide) table dominated by `NULL` values. Alternatively, creating an Entity-Attribute-Value (EAV) structure severely damages query performance and relational integrity.

## Alternatives
1. **Wide Table (Sparse Columns):** `ALTER TABLE` required for every new attribute; limits hit quickly.
2. **Entity-Attribute-Value (EAV):** Nightmarish SQL queries requiring multiple self-joins; poor performance.
3. **JSONB Column with GIN Index (Selected):** Store flexible attributes in a schema-less binary JSON field.

## Decision
We will add a `metadata JSONB` column to the `instruments` table. To ensure query performance, we will index this column using a Generalized Inverted Index (GIN) with the `jsonb_path_ops` operator class. 

## Consequences
- **Positive:** Product teams can add new asset-specific attributes without requiring DBA intervention or downtime for schema migrations.
- **Positive:** Containment queries (e.g., `metadata @> '{"sector":"Banking"}'`) run in sub-milliseconds via Bitmap Index Scans.
- **Negative:** Schema validation is deferred to the application layer (Spring Boot schemas), weakening database-level strictness. 

## AI Prompt
*“Design a PostgreSQL 16 schema solution for an instruments table where the required attributes vary wildly by asset class, ensuring we avoid EAV antipatterns and can still query efficiently.”*
