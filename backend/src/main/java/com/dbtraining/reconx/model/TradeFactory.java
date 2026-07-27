package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV023 — TradeFactory: build a TradeType by asset-class string
 *
 * WHAT:    Single entry point that takes an asset-class string + a map of
 *          field values and returns the right TradeType impl.
 * HOW:     Switch on the asset-class string, dispatch to the correct
 *          builder. Map values are cast/parsed per asset class.
 * WHY:     The Kafka consumer + REST POST endpoint both need to convert an
 *          untyped payload into a typed TradeType. Centralising the
 *          construction here means the parsing logic lives in one place.
 * OBSERVE: TradeFactoryTest.create_unknownAssetClass_throws fails when a
 *          new TradeType impl is added without updating the switch.
 * HINT:    Sealed hierarchy guarantees that every concrete TradeType MUST be
 *          listed in TradeType.permits — so this switch can be made
 *          exhaustive over assetClass enum.
 * ============================================================================
 */
public final class TradeFactory {

    private TradeFactory() { }

    /**
     * TODO(TICKET-ADV023):
     *   1. Parse assetClass string into TradeType.AssetClass enum (toUpperCase first).
     *   2. switch on the enum and dispatch to the matching equity/fx/bond/derivative
     *      helper below.
     *   3. The switch must be exhaustive — every TradeType.AssetClass case handled.
     */
    public static TradeType create(String assetClass, Map<String, Object> p) {
        Objects.requireNonNull(assetClass, "assetClass");
        Objects.requireNonNull(p, "payload");
        TradeType.AssetClass parsedAssetClass =
                TradeType.AssetClass.valueOf(assetClass.toUpperCase());

        return switch (parsedAssetClass) {
            case EQUITY -> equity(p);
            case FX -> fx(p);
            case BOND -> bond(p);
            case DERIVATIVE -> derivative(p);
        };
    }

    /**
     * TODO(TICKET-ADV023):
     *   Build an EquityTrade from the map. Expected keys: tradeRef, symbol,
     *   quantity, price, currency, side, tradeDate, counterpartyId.
     */
    private static EquityTrade equity(Map<String, Object> p) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(string(p, "tradeRef")))
                .instrumentSymbol(string(p, "symbol"))
                .quantity(decimal(p, "quantity"))
                .price(decimal(p, "price"))
                .currency(string(p, "currency"))
                .side(Side.valueOf(string(p, "side")))
                .tradeDate(LocalDate.parse(string(p, "tradeDate")))
                .counterpartyId(number(p, "counterpartyId").longValue())
                .build();
    }

    /**
     * TODO(TICKET-ADV023):
     *   Build an FXTrade from the map. Expected keys: tradeRef, ccy1, ccy2,
     *   notionalCcy1, fxRate, side, tradeDate, counterpartyId.
     */
    private static FXTrade fx(Map<String, Object> p) {
        return FXTrade.builder()
                .tradeRef(TradeRef.of(string(p, "tradeRef")))
                .ccy1(string(p, "ccy1"))
                .ccy2(string(p, "ccy2"))
                .notionalCcy1(decimal(p, "notionalCcy1"))
                .fxRate(decimal(p, "fxRate"))
                .side(Side.valueOf(string(p, "side")))
                .tradeDate(LocalDate.parse(string(p, "tradeDate")))
                .counterpartyId(number(p, "counterpartyId").longValue())
                .build();
    }

    /**
     * TODO(TICKET-ADV023):
     *   Build a BondTrade from the map. Expected keys: tradeRef, isin,
     *   faceValue, couponRate, maturityDate, currency, side, tradeDate,
     *   counterpartyId.
     */
    private static BondTrade bond(Map<String, Object> p) {
        return BondTrade.builder()
                .tradeRef(TradeRef.of(string(p, "tradeRef")))
                .isin(string(p, "isin"))
                .faceValue(decimal(p, "faceValue"))
                .couponRate(decimal(p, "couponRate"))
                .maturityDate(LocalDate.parse(string(p, "maturityDate")))
                .currency(string(p, "currency"))
                .side(Side.valueOf(string(p, "side")))
                .tradeDate(LocalDate.parse(string(p, "tradeDate")))
                .counterpartyId(number(p, "counterpartyId").longValue())
                .build();
    }

    /**
     * TODO(TICKET-ADV023):
     *   Build a DerivativeTrade from the map. Expected keys: tradeRef,
     *   underlying, strike, quantity, expiry, optionType, currency, side,
     *   tradeDate, counterpartyId.
     */
    private static DerivativeTrade derivative(Map<String, Object> p) {
        return DerivativeTrade.builder()
                .tradeRef(TradeRef.of(string(p, "tradeRef")))
                .underlying(string(p, "underlying"))
                .strike(decimal(p, "strike"))
                .quantity(decimal(p, "quantity"))
                .expiry(LocalDate.parse(string(p, "expiry")))
                .optionType(DerivativeTrade.OptionType.valueOf(string(p, "optionType")))
                .currency(string(p, "currency"))
                .side(Side.valueOf(string(p, "side")))
                .tradeDate(LocalDate.parse(string(p, "tradeDate")))
                .counterpartyId(number(p, "counterpartyId").longValue())
                .build();
    }

    private static Object required(Map<String, Object> payload, String key) {
        return Objects.requireNonNull(payload.get(key), key);
    }

    private static String string(Map<String, Object> payload, String key) {
        return String.class.cast(required(payload, key));
    }

    private static Number number(Map<String, Object> payload, String key) {
        return Number.class.cast(required(payload, key));
    }

    private static BigDecimal decimal(Map<String, Object> payload, String key) {
        return new BigDecimal(required(payload, key).toString());
    }
}
