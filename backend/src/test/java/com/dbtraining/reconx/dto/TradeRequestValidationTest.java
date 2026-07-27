package com.dbtraining.reconx.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TradeRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void negativeQuantityHasOnePositiveViolation() {
        TradeRequest request = new TradeRequest(
                "EQU-20260603-0001", 1L, 2L, "EQUITY", "BUY",
                new BigDecimal("-1"), new BigDecimal("100"), LocalDate.of(2026, 6, 3));

        assertThat(validator.validate(request))
                .singleElement()
                .satisfies(violation -> assertThat(violation.getPropertyPath().toString())
                        .isEqualTo("quantity"));
    }

    @Test
    void invalidTradeReferenceUsesPlatformPatternMessage() {
        TradeRequest request = new TradeRequest(
                "foo", 1L, 2L, "EQUITY", "BUY",
                BigDecimal.ONE, new BigDecimal("100"), LocalDate.of(2026, 6, 3));

        assertThat(validator.validate(request))
                .singleElement()
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("tradeRef must match AAA-YYYYMMDD-NNNN"));
    }

    private TradeRequest validRequest() {
        return new TradeRequest(
                "EQU-20260603-0001", 1L, 2L, "EQUITY", "BUY",
                BigDecimal.ONE, new BigDecimal("100"), LocalDate.of(2026, 6, 3));
    }
}
