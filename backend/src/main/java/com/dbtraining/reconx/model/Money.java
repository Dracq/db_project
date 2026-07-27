package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV024 — Immutable value object: Money
 *
 * WHAT:    Record bundling a {@link BigDecimal} amount with a {@link Currency}.
 *          Used everywhere a monetary value crosses a boundary (DTO, event,
 *          metric).
 * HOW:     Compact constructor enforces: non-null amount, non-null currency,
 *          non-negative amount. {@link BigDecimal} (not double) prevents
 *          accumulating floating-point error on aggregations.
 * WHY:     Passing raw BigDecimal around loses currency context — a USD 100
 *          can be silently added to a EUR 100. Money makes the mismatch
 *          fail at the type level: {@code plus()} throws if currencies differ.
 * OBSERVE: {@code Money.of("100.00","USD").plus(Money.of("50","EUR"))} throws.
 *          {@code Money.of("100","USD").plus(Money.of("50","USD"))} returns 150 USD.
 * ============================================================================
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative: " + amount);
        }
    }

    /**
     * Parse a decimal amount and ISO-4217 currency code into a money value.
     *
     * @param amount decimal representation accepted by {@link BigDecimal}.
     * @param currencyCode ISO-4217 code resolved by {@link Currency#getInstance(String)}.
     * @return a non-negative monetary value.
     * @throws NumberFormatException if the amount is not decimal text.
     * @throws IllegalArgumentException if the currency is unknown or the amount is negative.
     */
    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    /**
     * Add a monetary value in the same currency.
     *
     * @param other addend with the same ISO currency as this value.
     * @return a new money value; neither input is modified.
     * @throws NullPointerException if the addend is absent.
     * @throws IllegalArgumentException if currencies differ.
     */
    public Money plus(Money other) {
        Objects.requireNonNull(other, "other");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot add %s to %s: currency mismatch"
                            .formatted(other.currency, currency));
        }
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Scale this amount while retaining its currency.
     *
     * @param multiplier factor applied using {@link BigDecimal#multiply(BigDecimal)}.
     * @return the scaled non-negative money value.
     * @throws NullPointerException if the multiplier is absent.
     * @throws IllegalArgumentException if the result is negative.
     */
    public Money times(BigDecimal multiplier) {
        return new Money(amount.multiply(Objects.requireNonNull(multiplier, "multiplier")), currency);
    }
}
