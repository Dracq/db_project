package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TradeRefTest {

    @Test
    void of_acceptsPlatformTradeReferenceFormat() {
        assertThat(TradeRef.of("EQU-20260602-0001").value())
                .isEqualTo("EQU-20260602-0001");
    }

    @Test
    void of_rejectsInvalidFormat() {
        assertThatThrownBy(() -> TradeRef.of("foo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AAA-YYYYMMDD-NNNN");
    }

    @Test
    void toString_returnsRawReferenceValue() {
        assertThat(TradeRef.of("FXT-20260602-0001").toString())
                .isEqualTo("FXT-20260602-0001");
    }
}
