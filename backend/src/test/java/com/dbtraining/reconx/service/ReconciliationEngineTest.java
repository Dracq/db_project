package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV040 / ADV041 / ADV042 — TDD: write the test FIRST, then the impl.
 */
class ReconciliationEngineTest {

    private final ReconciliationEngine engine = new ReconciliationEngine();

    @Test
    void testReconcile_exactMatch_returnsMatched() {
        EquityTrade t1 = equity("EQU-20260603-0001", "100.0", "50");
        List<ReconResult> results = engine.reconcile(List.<TradeType>of(t1), List.<TradeType>of(t1), ReconciliationRule.EXACT);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
        assertThat(results.get(0).tradeRef()).isEqualTo("EQU-20260603-0001");
    }

    @Test
    void testReconcile_priceTolerance_withinThreshold() {
        EquityTrade internal = equity("EQU-20260603-0001", "100.00", "50");
        EquityTrade external = equity("EQU-20260603-0001", "100.50", "50");
        List<ReconResult> results = engine.reconcile(List.<TradeType>of(internal), List.<TradeType>of(external), ReconciliationRule.PRICE_TOLERANCE_1PCT);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
    }

    @Test
    void testReconcile_missingCounterpartyTrade_returnsBreak() {
        EquityTrade internal = equity("EQU-20260603-0001", "100.0", "50");
        List<ReconResult> results = engine.reconcile(List.<TradeType>of(internal), List.<TradeType>of(), ReconciliationRule.EXACT);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(results.get(0).discrepancyType()).isEqualTo("MISSING_EXTERNAL");
    }

    @Test
    void testReconcile_emptyInternal_returnsEmpty() {
        List<ReconResult> results = engine.reconcile(List.<TradeType>of(), List.<TradeType>of(), ReconciliationRule.EXACT);
        assertThat(results).isEmpty();
        
        List<ReconResult> nullResults = engine.reconcile(null, null, ReconciliationRule.EXACT);
        assertThat(nullResults).isEmpty();
    }

    @Test
    void testReconcileByCounterparty_parallelExecution() throws Exception {
        EquityTrade inCp1 = equity("EQU-20260603-0001", "100.0", "50");
        EquityTrade extCp1 = equity("EQU-20260603-0001", "100.0", "50");
        
        EquityTrade inCp2 = equity("EQU-20260603-0002", "200.0", "10");
        // missing external for cp2

        java.util.Map<Long, List<TradeType>> internalMap = java.util.Map.of(
                1L, List.<TradeType>of(inCp1),
                2L, List.<TradeType>of(inCp2)
        );
        java.util.Map<Long, List<TradeType>> externalMap = java.util.Map.of(
                1L, List.<TradeType>of(extCp1)
        );

        java.util.concurrent.CompletableFuture<List<ReconResult>> future = 
                engine.reconcileByCounterparty(internalMap, externalMap, ReconciliationRule.EXACT);
                
        List<ReconResult> results = future.get();
        assertThat(results).hasSize(2);
        assertThat(results.stream().map(ReconResult::status))
                .containsExactlyInAnyOrder(ReconResult.Status.MATCHED, ReconResult.Status.BREAK);
    }

    private EquityTrade equity(String ref, String price, String qty) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(qty))
                .currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
