package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FXTradeTest {

    @Test
    void builder_convertsNotionalIntoQuoteCurrency() {
        FXTrade trade = sampleTrade("FXT-20260603-0001", "EUR", "USD");

        assertThat(trade.notional().amount()).isEqualByComparingTo("108000");
        assertThat(trade.notional().currency().getCurrencyCode()).isEqualTo("USD");
        assertThat(trade.assetClass()).isEqualTo(TradeType.AssetClass.FX);
    }

    @Test
    void builder_rejectsMatchingCurrencies() {
        assertThatThrownBy(() -> sampleTrade("FXT-20260603-0001", "EUR", "EUR"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ccy1 and ccy2 must differ");
    }

    @Test
    void currencySetter_rejectsInvalidIsoCodeImmediately() {
        assertThatThrownBy(() -> FXTrade.builder().ccy1("EURR"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private FXTrade sampleTrade(String tradeRef, String ccy1, String ccy2) {
        return FXTrade.builder()
                .tradeRef(TradeRef.of(tradeRef))
                .ccy1(ccy1)
                .ccy2(ccy2)
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.08"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
