package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

class TradeTypeOrderingTest {

    @Test
    void naturalOrderIsNewestFirstAcrossAssetClasses() {
        TradeType equity = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));
        TradeType fx = fx("FXT-20260603-0001", LocalDate.of(2026, 6, 3));
        TradeType bond = bond("BND-20260602-0001", LocalDate.of(2026, 6, 2));
        TradeType derivative = derivative("DRV-20260604-0001", LocalDate.of(2026, 6, 4));

        List<TradeRef> orderedRefs = new ArrayList<>(
                new TreeSet<>(List.of(equity, fx, bond, derivative)))
                .stream().map(TradeType::tradeRef).toList();

        assertThat(orderedRefs).containsExactly(
                TradeRef.of("DRV-20260604-0001"),
                TradeRef.of("FXT-20260603-0001"),
                TradeRef.of("BND-20260602-0001"),
                TradeRef.of("EQU-20260601-0001"));
    }

    @Test
    void sameDateUsesTradeReferenceAsAscendingTiebreaker() {
        TradeType first = equity("EQU-20260603-0001", LocalDate.of(2026, 6, 3));
        TradeType second = equity("EQU-20260603-0002", LocalDate.of(2026, 6, 3));
        TradeType sameReference = fx("EQU-20260603-0001", LocalDate.of(2026, 6, 3));

        assertThat(first.compareTo(second)).isLessThan(0);
        assertThat(first.compareTo(sameReference)).isZero();
    }

    private EquityTrade equity(String ref, LocalDate tradeDate) {
        return EquityTrade.builder().tradeRef(TradeRef.of(ref)).instrumentSymbol("SAP.DE")
                .quantity(BigDecimal.ONE).price(BigDecimal.ONE).currency("EUR").side(Side.BUY)
                .tradeDate(tradeDate).counterpartyId(1L).build();
    }

    private FXTrade fx(String ref, LocalDate tradeDate) {
        return FXTrade.builder().tradeRef(TradeRef.of(ref)).ccy1("EUR").ccy2("USD")
                .notionalCcy1(BigDecimal.ONE).fxRate(BigDecimal.ONE).side(Side.BUY)
                .tradeDate(tradeDate).counterpartyId(1L).build();
    }

    private BondTrade bond(String ref, LocalDate tradeDate) {
        return BondTrade.builder().tradeRef(TradeRef.of(ref)).isin("US0378331005")
                .faceValue(BigDecimal.ONE).couponRate(BigDecimal.ZERO)
                .maturityDate(tradeDate.plusYears(1)).currency("USD").side(Side.BUY)
                .tradeDate(tradeDate).counterpartyId(1L).build();
    }

    private DerivativeTrade derivative(String ref, LocalDate tradeDate) {
        return DerivativeTrade.builder().tradeRef(TradeRef.of(ref)).underlying("AAPL")
                .strike(BigDecimal.ONE).quantity(BigDecimal.ONE).expiry(tradeDate.plusMonths(1))
                .optionType(DerivativeTrade.OptionType.CALL).currency("USD").side(Side.BUY)
                .tradeDate(tradeDate).counterpartyId(1L).build();
    }
}
