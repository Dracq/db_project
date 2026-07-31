package com.dbtraining.reconx.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeRequest(
    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}-\\d{8}-\\d{4}$", message = "tradeRef must match AAA-YYYYMMDD-NNNN")
    String tradeRef,
    @NotNull Long counterpartyId,
    @NotNull Long instrumentId,
    String assetClass,
    String side,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @NotNull @PastOrPresent LocalDate tradeDate
) {
    public TradeRequest(String tradeRef, Long counterpartyId, Long instrumentId, BigDecimal quantity, BigDecimal price, LocalDate tradeDate) {
        this(tradeRef, counterpartyId, instrumentId, "EQUITY", "BUY", quantity, price, tradeDate);
    }
}
