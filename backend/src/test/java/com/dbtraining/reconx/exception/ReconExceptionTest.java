package com.dbtraining.reconx.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReconExceptionTest {

    @Test
    void invalidTradeExceptionPreservesCause() {
        RuntimeException cause = new RuntimeException("invalid payload");

        InvalidTradeException exception = new InvalidTradeException("bad trade", cause);

        assertThat(exception).isInstanceOf(ReconException.class);
        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception).hasMessage("bad trade");
    }

    @Test
    void lookupAndDuplicateExceptionsPreserveCause() {
        Throwable cause = new IllegalStateException("database unavailable");

        assertThat(new TradeNotFoundException("EQU-20260603-0001", cause).getCause())
                .isSameAs(cause);
        assertThat(new DuplicateTradeRefException("EQU-20260603-0001", cause).getCause())
                .isSameAs(cause);
    }

    @Test
    void mismatchExceptionPreservesCause() {
        Throwable cause = new IllegalArgumentException("bad match");

        assertThat(new ReconciliationMismatchException("mismatch", cause).getCause())
                .isSameAs(cause);
    }
}
