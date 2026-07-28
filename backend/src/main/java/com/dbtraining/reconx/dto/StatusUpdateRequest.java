package com.dbtraining.reconx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * TICKET-ADV066 — PATCH /api/v1/trades/{id}/status body.
 */
public record StatusUpdateRequest(
    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(PENDING|MATCHED|UNMATCHED|DISPUTED|CANCELLED)$", message = "Invalid status value")
    String status
) {}
