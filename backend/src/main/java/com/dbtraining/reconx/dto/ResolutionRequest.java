package com.dbtraining.reconx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TICKET-ADV070 — PUT /api/v1/recon/results/{id}/resolve body.
 */
public record ResolutionRequest(
    @NotBlank(message = "Resolution note cannot be blank")
    @Size(max = 500, message = "Resolution note cannot exceed 500 characters")
    String note
) {}
