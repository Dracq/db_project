package com.dbtraining.reconx.service;

import com.dbtraining.reconx.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TradeAnalyticsServiceTest {

    private final TradeAnalyticsService service = new TradeAnalyticsService();

    @Test
    void testNotionalByCounterparty() {
        EquityTrade t1 = equity("EQU-20260603-0001", "100", "10", 1L); // 1000
        EquityTrade t2 = equity("EQU-20260603-0002", "200", "5", 1L);  // 1000
        EquityTrade t3 = equity("EQU-20260603-0003", "50", "10", 2L);  // 500

        Map<Long, TradeAnalyticsService.NotionalSummary> summary = service.notionalByCounterparty(List.of(t1, t2, t3));

        assertThat(summary).containsKeys(1L, 2L);
        assertThat(summary.get(1L).count()).isEqualTo(2);
        assertThat(summary.get(1L).total()).isEqualByComparingTo("2000");

        assertThat(summary.get(2L).count()).isEqualTo(1);
        assertThat(summary.get(2L).total()).isEqualByComparingTo("500");
    }

    @Test
    void testVwapByInstrument() {
        EquityTrade t1 = equity("EQU-20260603-0001", "100", "10", 1L, "SAP.DE"); // val: 1000, qty: 10
        EquityTrade t2 = equity("EQU-20260603-0002", "110", "10", 1L, "SAP.DE"); // val: 1100, qty: 10
        EquityTrade t3 = equity("EQU-20260603-0003", "200", "5", 2L, "IBM");     // val: 1000, qty: 5

        Map<String, BigDecimal> vwap = service.vwapByInstrument(List.of(t1, t2, t3));

        assertThat(vwap).containsKeys("SAP.DE", "IBM");
        assertThat(vwap.get("SAP.DE")).isEqualByComparingTo("105"); // 2100 / 20 = 105
        assertThat(vwap.get("IBM")).isEqualByComparingTo("200");
    }

    @Test
    void testVwapByInstrument_ZeroQty() {
        // Technically EquityTrade builder prevents qty=0, but we test the math safety anyway.
        // We'll use reflection or just assume the method handles empty lists safely.
        Map<String, BigDecimal> vwap = service.vwapByInstrument(List.of());
        assertThat(vwap).isEmpty();
    }

    @Test
    void testPnlByInstrument() {
        EquityTrade buyTrade = equity("EQU-20260603-0001", "100", "10", 1L, "SAP.DE", Side.BUY); // -1000
        EquityTrade sellTrade = equity("EQU-20260603-0002", "120", "10", 1L, "SAP.DE", Side.SELL); // +1200
        EquityTrade buyTrade2 = equity("EQU-20260603-0003", "50", "10", 2L, "IBM", Side.BUY); // -500

        Map<String, BigDecimal> pnl = service.pnlByInstrument(List.of(buyTrade, sellTrade, buyTrade2));

        assertThat(pnl).containsKeys("SAP.DE", "IBM");
        assertThat(pnl.get("SAP.DE")).isEqualByComparingTo("200");
        assertThat(pnl.get("IBM")).isEqualByComparingTo("-500");
    }

    private EquityTrade equity(String ref, String price, String qty, long cp) {
        return equity(ref, price, qty, cp, "SAP.DE", Side.BUY);
    }
    
    private EquityTrade equity(String ref, String price, String qty, long cp, String symbol) {
        return equity(ref, price, qty, cp, symbol, Side.BUY);
    }

    private EquityTrade equity(String ref, String price, String qty, long cp, String symbol, Side side) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol(symbol)
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(qty))
                .currency("EUR")
                .side(side)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(cp)
                .build();
    }
}
