```mermaid
erDiagram
    COUNTERPARTIES ||--o{ TRADES : "executes"
    INSTRUMENTS    ||--o{ TRADES : "covers"
    TRADES         ||--o{ SETTLEMENTS : "settles via"
    TRADES         ||--o{ RECON_BREAKS : "may produce"
    RECON_JOBS     ||--o{ RECON_BREAKS : "detected by"
    USERS          ||--o{ AUDIT_LOG : "actor"
    TRADES         ||--o{ AUDIT_LOG : "audited"

    COUNTERPARTIES {
        bigint id PK
        varchar name
        char lei_code UK
        varchar region
        timestamp created_at
    }

    INSTRUMENTS {
        bigint id PK
        varchar symbol UK
        varchar name
        varchar asset_class
        char currency
        char isin UK
        jsonb metadata "JSONB column"
    }

    TRADES {
        bigint id PK
        varchar trade_ref UK
        bigint instrument_id FK
        bigint counterparty_id FK
        varchar asset_class
        varchar side
        numeric quantity
        numeric price
        date trade_date "PARTITION KEY (ADV007)"
        varchar status
        timestamp deleted_at
        timestamp created_at
        timestamp modified_at
    }

    SETTLEMENTS {
        bigint id PK
        bigint trade_id FK
        date settlement_date
        numeric amount
        varchar status
    }

    RECON_BREAKS {
        bigint id PK
        bigint trade_id FK
        bigint recon_job_id FK
        varchar discrepancy_type
        varchar status
        timestamp detected_at
        timestamp resolved_at
        varchar resolution_note
    }

    RECON_JOBS {
        bigint id PK
        varchar job_id UK
        bigint triggered_by FK
        date from_date
        date to_date
        varchar status
        timestamp started_at
        timestamp finished_at
        int trades_processed
        int breaks_detected
    }

    AUDIT_LOG {
        bigint id PK
        varchar event_id UK
        varchar trade_ref
        varchar event_type
        timestamp event_timestamp
        varchar changed_by "No FK to users"
        text before_state
        text after_state
    }

    USERS {
        bigint id PK
        varchar email UK
        varchar password_hash
        varchar role
        boolean enabled
        timestamp created_at
    }
```
