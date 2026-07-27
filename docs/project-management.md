# ReconX Project Management Workflow

This document models the project management structure mapping the enterprise delivery for Day 1 Database Architecture (Tickets ADV001-ADV017).

## Issue Tracker Structure (Jira / GitHub Projects)

### Epic: Foundation & Database Architecture (Day 1)
**Goal:** Establish enterprise repository governance, C4 architectural documentation, and the foundational PostgreSQL 16 schema optimized via Liquibase.

#### Stories & Tasks
- **Story: Repository & Governance**
  - **Task ADV001:** Setup `.github/CODEOWNERS` and Branch Protection Policies.
- **Story: Architecture Documentation**
  - **Task ADV002:** C4 Context Diagram (`c4-context.md`).
  - **Task ADV003:** C4 Container Diagram (`c4-container.md`).
  - **Task ADV004:** C4 Component Diagram for `recon-service API`.
  - **Task ADV015:** AI-Assisted Architecture Decision Records (ADRs).
- **Story: Database Schema Design**
  - **Task ADV006:** Enterprise ER Model (8 Entities).
  - **Task ADV009:** JSONB metadata support for Instruments.
- **Story: Database Performance & Partitioning**
  - **Task ADV007:** Range Partitioning implementation for `trades` table.
  - **Task ADV008:** Concurrent Materialized View for Analytics.
  - **Task ADV010:** VWAP Window Function queries.
  - **Task ADV011:** Trade Lifecycle Recursive CTE.
- **Story: Liquibase Governance**
  - **Task ADV012:** Master changelog architecture.
  - **Task ADV013:** Declarative Rollback support & Database Tagging.
  - **Task ADV014:** Enterprise Preconditions & Failsafes.
- **Story: Data Seeding**
  - **Task ADV017:** Generate deterministic seed data (Counterparties, Instruments, 500 Trades).

## State Transition Workflow
To enforce strict quality gates, tickets must follow this lifecycle:

1. **Backlog:** Ticket is refined and requirements are clear.
2. **In Progress:** Developer claims the ticket. Branch created (`feat/ADV0XX-description`).
3. **Review:** Code pushed, PR opened. GitHub Actions run linting and dry-run migrations. Tech Lead (CODEOWNERS) must approve.
4. **Done:** Merged into `develop` or `main`.

*Note: As an enterprise project, skipping the Review phase is prohibited.*
