package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TradeEqualityTest {

    @Test
    void matchingTradeReferencesCollapseSameConcreteTypeInHashSet() {
        Set<TradeType> trades = new HashSet<>(List.of(
                equity("EQU-20260603-0001", "100"),
                equity("EQU-20260603-0001", "200")));

        assertThat(trades).hasSize(1);
    }

    @Test
    void equalityIsSpecificToConcreteTradeType() {
        TradeRef ref = TradeRef.of("EQU-20260603-0001");

        assertThat(equity(ref.value(), "100")).isNotEqualTo(fx(ref.value()));
    }

    private EquityTrade equity(String ref, String quantity) {
        return EquityTrade.builder().tradeRef(TradeRef.of(ref)).instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal(quantity)).price(BigDecimal.ONE).currency("EUR")
                .side(Side.BUY).tradeDate(LocalDate.of(2026, 6, 3)).counterpartyId(1L).build();
    }

    private FXTrade fx(String ref) {
        return FXTrade.builder().tradeRef(TradeRef.of(ref)).ccy1("EUR").ccy2("USD")
                .notionalCcy1(BigDecimal.ONE).fxRate(BigDecimal.ONE).side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3)).counterpartyId(1L).build();
    }
}
