# ReconX Project Management Workflow

This document models the project management structure mapping the enterprise delivery for Day 1 Database Architecture (Tickets ADV001-ADV017).

## Issue Tracker Structure (Jira / GitHub Projects)

### Epic 1: Repository Governance & Infrastructure
**Goal:** Establish enterprise repository governance and CI/CD foundations.
- **Task ADV001:** Setup `.github/CODEOWNERS` and Branch Protection Policies.
  - **Owner:** Platform Engineering Lead
  - **Story Points:** 2
- **Task ADV016:** Project Management Structure setup.
  - **Owner:** Scrum Master
  - **Story Points:** 1

### Epic 2: Architecture Documentation
**Goal:** Model system architecture via C4 and ADRs.
- **Task ADV002:** C4 Context Diagram (`c4-context.md`).
  - **Owner:** Enterprise Architect
  - **Story Points:** 3
- **Task ADV003:** C4 Container Diagram (`c4-container.md`).
  - **Owner:** Enterprise Architect
  - **Story Points:** 3
- **Task ADV004:** C4 Component Diagram for `recon-service API`.
  - **Owner:** Enterprise Architect
  - **Story Points:** 5
- **Task ADV015:** AI-Assisted Architecture Decision Records (ADRs).
  - **Owner:** Principal DBA
  - **Story Points:** 3

### Epic 3: Database Architecture & Liquibase Governance
**Goal:** Build the high-performance PostgreSQL 16 schema.
- **Task ADV006:** Enterprise ER Model (8 Entities).
  - **Owner:** Principal DBA
  - **Story Points:** 5
- **Task ADV007:** Range Partitioning implementation for `trades` table.
  - **Owner:** Database Engineer
  - **Story Points:** 5
- **Task ADV008:** Concurrent Materialized View for Analytics.
  - **Owner:** Data Engineer
  - **Story Points:** 3
- **Task ADV009:** JSONB metadata support for Instruments.
  - **Owner:** Database Engineer
  - **Story Points:** 2
- **Task ADV010:** VWAP Window Function queries.
  - **Owner:** Data Engineer
  - **Story Points:** 3
- **Task ADV011:** Trade Lifecycle Recursive CTE.
  - **Owner:** Data Engineer
  - **Story Points:** 5
- **Task ADV012:** Master changelog architecture.
  - **Owner:** Platform Engineering Lead
  - **Story Points:** 2
- **Task ADV013:** Declarative Rollback support & Database Tagging.
  - **Owner:** Platform Engineering Lead
  - **Story Points:** 2
- **Task ADV014:** Enterprise Preconditions & Failsafes.
  - **Owner:** Platform Engineering Lead
  - **Story Points:** 2
- **Task ADV017:** Generate deterministic seed data.
  - **Owner:** QA Lead
  - **Story Points:** 3

## State Transition Workflow
To enforce strict quality gates, tickets must follow this lifecycle:

1. **Backlog:** Ticket is refined and requirements are clear.
2. **In Progress:** Developer claims the ticket. Branch created (`feat/ADV0XX-description`).
3. **Review:** Code pushed, PR opened. GitHub Actions run linting and dry-run migrations. Tech Lead (CODEOWNERS) must approve.
4. **Done:** Merged into `develop` or `main`.
