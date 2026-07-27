package com.dbtraining.reconx.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Internal base for state shared by all trade implementations.
 *
 * <p>Concrete asset classes are introduced in their respective tickets. This
 * class centralises the non-null guard for the fields that every trade needs.
 * It intentionally has package visibility: callers work against
 * {@link TradeType}, not this implementation detail.</p>
 */
abstract class Trade {

    private final TradeRef tradeRef;
    private final Money notional;
    private final LocalDate tradeDate;

    Trade(TradeRef tradeRef, Money notional, LocalDate tradeDate) {
        this.tradeRef = Objects.requireNonNull(tradeRef, "tradeRef");
        this.notional = Objects.requireNonNull(notional, "notional");
        this.tradeDate = Objects.requireNonNull(tradeDate, "tradeDate");
    }

    final TradeRef sharedTradeRef() {
        return tradeRef;
    }

    final Money sharedNotional() {
        return notional;
    }

    final LocalDate sharedTradeDate() {
        return tradeDate;
    }
}
