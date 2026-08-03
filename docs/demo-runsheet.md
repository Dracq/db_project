# 20-Minute Demo Runsheet & Rehearsal Log

> **ReconX — Enterprise Trade Reconciliation Platform**  
> Deutsche Bank TDI 2026 Advanced Track

---

## 20-Minute Timing Breakdown

| Time Window | Duration | Speaker / Owner | Action & Screen State |
|---|---|---|---|
| **00:00 - 00:30** | 30 s | Team Lead | Slide 1: Title slide & team introductions |
| **00:30 - 03:00** | 150 s | Team Lead | Slides 2-4: Problem statement, Mermaid runtime architecture, and layered Tech Stack |
| **03:00 - 04:00** | 60 s | Presenter | Switch screen from slides to browser. Open React UI at `http://localhost:5173`. Perform JWT login as `trader@db.com`. Show 200 OK in DevTools Network tab. |
| **04:00 - 05:30** | 90 s | Presenter | Submit trade (`SMOKE-DEMO-01`) via React UI form. Show Network payload and 201 Created response. |
| **05:30 - 07:00** | 90 s | Backend Lead | Switch tab to Kafdrop (`http://localhost:9000`). Inspect `trade-events` topic and display published JSON payload. |
| **07:00 - 08:30** | 90 s | DevOps Lead | Switch tab to Grafana (`http://localhost:3000`). Show `trade_created_total` and `recon_break_count` panels ticking up. |
| **08:30 - 10:00** | 90 s | Backend Lead | Switch terminal to backend logs. Highlight `Liquibase` execution line and PostgreSQL `audit_log` row via `psql`. |
| **10:00 - 11:00** | 60 s | Team Lead | Transition back to slides. Slide 7: CI/CD Pipeline & GitHub Actions workflow. |
| **11:00 - 12:30** | 90 s | Backend Eng 1 | Code Walkthrough: `TradeController.java` & DTO validation annotations. |
| **12:30 - 14:00** | 90 s | Backend Eng 2 | Code Walkthrough: `ReconConsumer.java` & Kafka listener break logic. |
| **14:00 - 15:00** | 60 s | Frontend Eng | Code Walkthrough: `useTradeStream.js` custom React hook. |
| **15:00 - 16:00** | 60 s | All Engineers | Slide 9: Retrospective learnings — each engineer presents 1 key takeaway. |
| **16:00 - 20:00** | 240 s | Team Lead & Team | Slide 10: Q&A session with repo URL displayed. Lead routes questions. |

---

## Rehearsal Log

### Rehearsal 1 (Chaos Monkey Test) — Completed 15:30
- **Chaos Injected**: Simulated network drop / transient database disconnect during trade submission.
- **Recovery Executed**: System reported clear error toast; team gracefully demonstrated backup flow using pre-recorded smoke test execution.
- **Adjustments Made**: Added explicit screen switch timestamps and fallback tabs.

### Rehearsal 2 (Instructor Q&A Test) — Completed 16:15
- **Mock Questions Tested**:
  1. *Q: How does the application handle duplicate trade submissions on Kafka consumer restarts?*  
     *A: `enable.idempotence` is enabled on the producer, and the `ReconConsumer` uses tradeRef deduplication checks against Postgres.*
  2. *Q: Why validate Liquibase in CI before tests run?*  
     *A: To catch changelog checksum drift or XML parse errors in 30 seconds rather than debugging cryptic test failures.*
- **Outcome**: Completed inside the 20-minute cap (19m 15s total elapsed).
