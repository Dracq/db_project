package com.dbtraining.reconx.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TradeResponse(
    Long id,
    String tradeRef,
    Long counterpartyId,
    String counterpartyName,
    Long instrumentId,
    String instrumentSymbol,
    String assetClass,
    String side,
    BigDecimal quantity,
    BigDecimal price,
    LocalDate tradeDate,
    String status,
    Instant createdAt,
    Instant modifiedAt
) {}
