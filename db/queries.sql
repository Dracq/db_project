-- ============================================================================
-- TICKET-ADV010 — VWAP per instrument per day (window function)
-- WHAT:    Calculates the Volume-Weighted Average Price (VWAP) per instrument per day
--          without collapsing rows via GROUP BY.
-- HOW:     Uses SUM() OVER (PARTITION BY instrument_id, trade_date).
-- WHY:     Window functions allow us to retain per-row detail (trade_id, price, quantity)
--          while calculating aggregates over a partition of rows. This is superior to GROUP BY
--          when we need both the aggregate and the individual row data.
-- ============================================================================
SELECT
    t.id AS trade_id,
    t.trade_ref,
    t.instrument_id,
    t.trade_date,
    t.price,
    t.quantity,
    (t.price * t.quantity) AS notional,
    SUM(t.price * t.quantity) OVER (PARTITION BY t.instrument_id, t.trade_date)
        / NULLIF(SUM(t.quantity) OVER (PARTITION BY t.instrument_id, t.trade_date), 0)
        AS vwap
FROM trades t
WHERE t.deleted_at IS NULL
ORDER BY t.trade_date DESC, t.instrument_id, t.created_at;


-- ============================================================================
-- TICKET-ADV011 — Recursive CTE: trade lifecycle
-- WHAT:    Rolls up trade lifecycle stages (EXECUTION -> CONFIRMATION -> 
--          SETTLEMENT -> RECON_BREAK -> RESOLUTION).
-- HOW:     Uses WITH RECURSIVE and a LATERAL join to traverse dependent tables.
-- WHY:     PostgreSQL evaluates recursive CTEs by first executing the non-recursive
--          anchor term, then repeatedly executing the recursive term, appending results 
--          until no new rows are produced. Infinite recursion is prevented by the
--          termination guard (WHERE tl.step < 5).
-- ============================================================================
WITH RECURSIVE trade_lifecycle AS (
    -- Anchor: EXECUTION
    SELECT 
        t.id AS trade_id, 
        t.trade_ref, 
        1 AS step, 
        'EXECUTION' AS stage, 
        t.created_at AS event_time, 
        t.status AS status
    FROM trades t
    WHERE t.deleted_at IS NULL

    UNION ALL

    -- Recursive step: Traversing the lifecycle tables
    SELECT 
        tl.trade_id, 
        tl.trade_ref, 
        tl.step + 1 AS step, 
        next_event.stage, 
        next_event.event_time, 
        next_event.status
    FROM trade_lifecycle tl
    JOIN LATERAL (
        SELECT 'CONFIRMATION' AS stage, modified_at AS event_time, status 
        FROM trades 
        WHERE id = tl.trade_id AND tl.step = 1
        
        UNION ALL
        
        SELECT 'SETTLEMENT', settlement_date::timestamp, status 
        FROM settlements 
        WHERE trade_id = tl.trade_id AND tl.step = 2
        
        UNION ALL
        
        SELECT 'RECON_BREAK', detected_at, status 
        FROM recon_breaks 
        WHERE trade_id = tl.trade_id AND tl.step = 3
        
        UNION ALL
        
        SELECT 'RESOLUTION', resolved_at, resolution_note 
        FROM recon_breaks 
        WHERE trade_id = tl.trade_id AND tl.step = 4 AND resolved_at IS NOT NULL
    ) AS next_event ON true
    -- Termination guard
    WHERE tl.step < 5
)
SELECT trade_id, step, stage, event_time, status 
FROM trade_lifecycle 
ORDER BY trade_id, step;


-- ============================================================================
-- ADV008 — REFRESH the daily-summary materialised view (concurrently)
-- ============================================================================
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_daily_recon_summary;


-- ============================================================================
-- ADV009 — JSONB lookup: which instruments have sector = 'Banking'?
-- ============================================================================
SELECT id, symbol, metadata
FROM instruments
WHERE metadata @> '{"sector":"Banking"}'::jsonb;
