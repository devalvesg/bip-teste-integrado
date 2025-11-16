package com.example.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transfer Request DTO")
public class TransferRequest {

    @NotNull(message = "Source beneficiary ID is required")
    @Schema(description = "Source beneficiary ID", example = "1")
    private Long fromBeneficioId;

    @NotNull(message = "Destination beneficiary ID is required")
    @Schema(description = "Destination beneficiary ID", example = "2")
    private Long toBeneficioId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Transfer amount", example = "100.00")
    private BigDecimal amount;
}

