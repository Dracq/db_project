package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class BondTradeTest {

    @Test
    void builder_buildsBondWithFaceValueAsNotional() {
        BondTrade trade = sampleBond(LocalDate.of(2031, 6, 3), "US0378331005");

        assertThat(trade.notional().amount()).isEqualByComparingTo("1000000");
        assertThat(trade.notional().currency().getCurrencyCode()).isEqualTo("USD");
        assertThat(trade.assetClass()).isEqualTo(TradeType.AssetClass.BOND);
    }

    @Test
    void builder_rejectsMaturityOnOrBeforeTradeDate() {
        assertThatThrownBy(() -> sampleBond(LocalDate.of(2026, 6, 3), "US0378331005"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("maturityDate must be after tradeDate");
    }

    @Test
    void builder_rejectsNonStandardIsinLength() {
        assertThatThrownBy(() -> sampleBond(LocalDate.of(2031, 6, 3), "US037833100"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("isin must contain 12 characters");
    }

    private BondTrade sampleBond(LocalDate maturityDate, String isin) {
        return BondTrade.builder()
                .tradeRef(TradeRef.of("BND-20260603-0001"))
                .isin(isin)
                .faceValue(new BigDecimal("1000000"))
                .couponRate(new BigDecimal("0.05"))
                .maturityDate(maturityDate)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
