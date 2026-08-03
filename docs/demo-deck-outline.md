# Demo Deck — 10 Slides Outline & Presentation Notes

> **ReconX — Enterprise Trade Reconciliation Platform**  
> Deutsche Bank — TDI 2026 Graduate Technical Training Programme (Advanced Track)

---

### Slide 1: Title & Team Introduction
- **Header**: ReconX — Enterprise Trade Reconciliation Platform
- **Bullets**:
  - Deutsche Bank TDI 2026 Graduate Technical Training Programme
  - Advanced Track (10-Day Case Study)
  - Team: Lead, Backend Engineer, Frontend Engineer, DevOps Engineer
- **Speaker Note**: *Welcome everyone. Today we are presenting ReconX, an enterprise trade reconciliation platform built over 10 days across 165 technical tickets.*

---

### Slide 2: Problem Statement & Business Context
- **Header**: Business Problem: Ops Reconciliation Pain Points
- **Bullets**:
  - Operations teams process thousands of trades daily across disparate counterparty feeds
  - Manual mismatch detection leads to costly settlement breaks and regulatory risk
  - Need real-time event streaming, auto-reconciliation, and instant break alert resolution
- **Speaker Note**: *In Deutsche Bank operations, trade mismatches cost time and money. ReconX automates reconciliation instantly upon event arrival.*

---

### Slide 3: System Architecture
- **Header**: 7-Service Microservice & Streaming Architecture
- **Bullets**:
  - React 19 SPA frontend with Vite and Server-Sent Events (SSE) live feed
  - Spring Boot 3 Java 25 REST backend with Liquibase & PostgreSQL 16
  - Apache Kafka multi-topic event pipeline with Dead Letter Queue (DLQ)
- **Speaker Note**: *Here is our runtime architecture. The browser talks to Spring Boot via nginx proxy, events flow to Kafka, and consumers reconcile trades directly into Postgres.*

---

### Slide 4: Technology Stack by Layer
- **Header**: Enterprise Tech Stack by Architectural Layer
- **Bullets**:
  - **Data & Persistence**: PostgreSQL 16, Liquibase, Hibernate Envers, Hypersistence JSONB
  - **Core & Streaming**: Java 25 (Virtual Threads, Sealed Classes), Spring Boot 3, Apache Kafka
  - **UI & Observability**: React 19, Vanilla CSS Design System, Prometheus, Grafana
- **Speaker Note**: *We grouped our tech stack by layer. Note our use of Java 25 sealed classes for trade hierarchies and virtual threads for parallel processing.*

---

### Slide 5: Live Demo — Authentication & Trade Submission
- **Header**: Live Demo Step 1: JWT Login & Trade Entry
- **Bullets**:
  - Role-based Access Control (RBAC) with JWT authentication (Trader vs Analyst)
  - Interactive React trade creation interface with real-time field validation
  - Instant REST API submission (`POST /api/v1/trades`) with audit logging
- **Speaker Note**: *Now let's jump into the live system. We log in as trader@db.com and submit a new trade event.*

---

### Slide 6: Live Demo — Kafka Streaming & Auto-Reconciliation
- **Header**: Live Demo Step 2: Event Pipeline & Auto-Reconciliation
- **Bullets**:
  - Immediate `trade-events` Kafka topic publication upon POST
  - Automated `ReconConsumer` matching against counterparty feed
  - Real-time alert generation on `system-alerts` topic for breaks
- **Speaker Note**: *Watch as the submitted trade triggers a Kafka event. Our consumer matches it, and if a break exists, it flags it for analyst resolution.*

---

### Slide 7: CI/CD Pipeline & Automated Quality Gates
- **Header**: Automated Delivery Pipeline (GitHub Actions & GHCR)
- **Bullets**:
  - Checkstyle static analysis and Liquibase pre-flight changelog validation
  - JaCoCo automated line coverage gate enforced at >= 85%
  - Automated multi-stage Docker image build and push to GHCR on main merge
- **Speaker Note**: *Our GitHub Actions pipeline enforces quality before deployment: Checkstyle, Liquibase validation, 85% test coverage, and automated GHCR image publishing.*

---

### Slide 8: Real-Time Observability & Load Performance
- **Header**: Observability & 200 VU Load Test Performance
- **Bullets**:
  - **Baseline**: Idle stack operating at < 50ms p95 latency
  - **Under Load**: 200 concurrent VUs sustained at 285 RPS with < 800ms p95 latency
  - **Recovery**: Consumer lag drains to zero within 30s post-test
- **Speaker Note**: *These Grafana panels show our system under load. During a 200 VU k6 test, p95 latency stayed under 800ms and recovered smoothly within 30 seconds.*

---

### Slide 9: Key Learnings & Engineering Retrospective
- **Header**: Key Technical Takeaways & Retrospective
- **Bullets**:
  - Multi-stage Docker optimization reduced backend image footprint from 600MB to 220MB
  - Early Liquibase schema locking prevented environment drift across microservices
  - Kafka DLQ pattern ensured resilient handling of malformed trade payloads
- **Speaker Note**: *Our main learnings: multi-stage Docker builds kept images lean, early database schema locking prevented drift, and DLQs handled corrupt events gracefully.*

---

### Slide 10: Conclusion & Q&A
- **Header**: Summary & Open Discussion
- **Bullets**:
  - Live 7-service stack containerised, monitored, and fully tested
  - Production-ready CI/CD shipping tagged Docker images (`v1.0.0`)
  - Repository & Codebase: `https://github.com/Dracq/db_project`
- **Speaker Note**: *Thank you! We are happy to take questions on any architectural layer or code implementation.*
