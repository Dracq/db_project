package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void plus_returnsNewValueWithoutChangingEitherOperand() {
        Money hundredUsd = Money.of("100", "USD");
        Money fiftyUsd = Money.of("50", "USD");

        Money total = hundredUsd.plus(fiftyUsd);

        assertThat(total).isEqualTo(new Money(new BigDecimal("150"), Currency.getInstance("USD")));
        assertThat(hundredUsd.amount()).isEqualByComparingTo("100");
        assertThat(fiftyUsd.amount()).isEqualByComparingTo("50");
    }

    @Test
    void plus_rejectsCurrencyMismatch() {
        assertThatThrownBy(() -> Money.of("100", "USD").plus(Money.of("50", "EUR")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency mismatch");
    }

    @Test
    void constructor_rejectsNegativeAmount() {
        assertThatThrownBy(() -> Money.of("-0.01", "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
}
