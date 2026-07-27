package com.dbtraining.reconx.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TradeFactoryTest {

    @Test
    void create_equityBuildsTypedTrade() {
        TradeType trade = TradeFactory.create("EQUITY", equityPayload());

        assertThat(trade).isInstanceOf(EquityTrade.class);
        assertThat(trade.assetClass()).isEqualTo(TradeType.AssetClass.EQUITY);
    }

    @Test
    void create_unknownAssetClassFailsBeforeBuilderRuns() {
        assertThatThrownBy(() -> TradeFactory.create("FOO", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_missingRequiredFieldNamesTheField() {
        Map<String, Object> payload = equityPayload();
        payload.remove("price");

        assertThatThrownBy(() -> TradeFactory.create("EQUITY", payload))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("price");
    }

    private Map<String, Object> equityPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("tradeRef", "EQU-20260603-0001");
        payload.put("symbol", "SAP.DE");
        payload.put("quantity", "100");
        payload.put("price", "100");
        payload.put("currency", "EUR");
        payload.put("side", "BUY");
        payload.put("tradeDate", "2026-06-03");
        payload.put("counterpartyId", 1L);
        return payload;
    }
}
