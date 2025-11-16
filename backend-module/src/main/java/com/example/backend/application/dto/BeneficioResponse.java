package com.example.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Beneficiary Response DTO")
public class BeneficioResponse {

    @Schema(description = "Beneficiary ID", example = "1")
    private Long id;

    @Schema(description = "Beneficiary name", example = "John Doe")
    private String name;

    @Schema(description = "Account balance", example = "1500.50")
    private BigDecimal balance;

    @Schema(description = "Version for optimistic locking", example = "0")
    private Long version;
}

