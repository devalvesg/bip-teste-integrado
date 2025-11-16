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
@Schema(description = "Create Beneficiary Request DTO")
public class CreateBeneficioRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    @Schema(description = "Beneficiary name", example = "John Doe")
    private String name;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", message = "Balance must be greater than or equal to 0")
    @Schema(description = "Initial account balance", example = "1000.00")
    private BigDecimal balance;
}

