package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DerivativeTradeTest {

    @Test
    void builder_buildsHistoricalExpiredOptionWhenTradeChronologyIsValid() {
        DerivativeTrade trade = sampleTrade(
                LocalDate.of(2020, 1, 2), LocalDate.of(2021, 1, 2));

        assertThat(trade.notional().amount()).isEqualByComparingTo("15000");
        assertThat(trade.assetClass()).isEqualTo(TradeType.AssetClass.DERIVATIVE);
        assertThat(trade.optionType()).isEqualTo(DerivativeTrade.OptionType.CALL);
    }

    @Test
    void builder_rejectsExpiryOnOrBeforeTradeDate() {
        assertThatThrownBy(() -> sampleTrade(
                LocalDate.of(2026, 6, 3), LocalDate.of(2026, 6, 3)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("expiry must be after tradeDate");
    }

    @Test
    void builder_rejectsNonPositiveStrike() {
        assertThatThrownBy(() -> DerivativeTrade.builder()
                .tradeRef(TradeRef.of("DRV-20260603-0001"))
                .underlying("AAPL")
                .strike(BigDecimal.ZERO)
                .quantity(new BigDecimal("100"))
                .expiry(LocalDate.of(2027, 6, 3))
                .optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("strike must be > 0");
    }

    private DerivativeTrade sampleTrade(LocalDate tradeDate, LocalDate expiry) {
        return DerivativeTrade.builder()
                .tradeRef(TradeRef.of("DRV-20260603-0001"))
                .underlying("AAPL")
                .strike(new BigDecimal("150"))
                .quantity(new BigDecimal("100"))
                .expiry(expiry)
                .optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(tradeDate)
                .counterpartyId(1L)
                .build();
    }
}
