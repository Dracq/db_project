package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class TradeToStringTest {

    @Test
    void routineTradeLogsExcludeCounterpartyAndUsePlainDecimalRendering() {
        for (TradeType trade : List.of(equity(), fx(), bond(), derivative())) {
            String logLine = trade.toString();
            assertThat(logLine).contains(trade.tradeRef().value());
            assertThat(logLine).doesNotContain("987654321").doesNotContain("E+");
        }
    }

    private EquityTrade equity() {
        return EquityTrade.builder().tradeRef(TradeRef.of("EQU-20260603-0001"))
                .instrumentSymbol("SAP.DE").quantity(new BigDecimal("1E+2"))
                .price(new BigDecimal("1.005E+2")).currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3)).counterpartyId(987654321L).build();
    }

    private FXTrade fx() {
        return FXTrade.builder().tradeRef(TradeRef.of("FXT-20260603-0001")).ccy1("EUR").ccy2("USD")
                .notionalCcy1(new BigDecimal("1E+5")).fxRate(new BigDecimal("1.08"))
                .side(Side.BUY).tradeDate(LocalDate.of(2026, 6, 3)).counterpartyId(987654321L).build();
    }

    private BondTrade bond() {
        return BondTrade.builder().tradeRef(TradeRef.of("BND-20260603-0001")).isin("US0378331005")
                .faceValue(new BigDecimal("1E+6")).couponRate(new BigDecimal("0.05"))
                .maturityDate(LocalDate.of(2031, 6, 3)).currency("USD").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3)).counterpartyId(987654321L).build();
    }

    private DerivativeTrade derivative() {
        return DerivativeTrade.builder().tradeRef(TradeRef.of("DRV-20260603-0001")).underlying("AAPL")
                .strike(new BigDecimal("1.5E+2")).quantity(new BigDecimal("1E+2"))
                .expiry(LocalDate.of(2027, 6, 3)).optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD").side(Side.BUY).tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(987654321L).build();
    }
}
