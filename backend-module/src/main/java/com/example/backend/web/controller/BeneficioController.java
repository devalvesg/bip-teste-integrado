package com.example.backend.web.controller;

import com.example.api.dto.TransferRequest;
import com.example.api.exception.BeneficiaryNotFoundException;
import com.example.api.exception.InsufficientBalanceException;
import com.example.backend.application.dto.ApiResponse;
import com.example.backend.application.dto.BeneficioResponse;
import com.example.backend.application.dto.CreateBeneficioRequest;
import com.example.backend.application.service.BeneficioService;
import com.example.backend.application.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/beneficios")
@RequiredArgsConstructor
@Tag(name = "Beneficiary Management", description = "Endpoints for managing beneficiaries and transfers")
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Create a new beneficiary", description = "Creates a new beneficiary with initial balance")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Beneficiary created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BeneficioResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            )
    })
    public ResponseEntity<ApiResponse<BeneficioResponse>> createBeneficio(
            @Valid @RequestBody CreateBeneficioRequest request) {
        log.info("POST /api/beneficios - Creating new beneficiary");

        BeneficioResponse response = beneficioService.createBeneficio(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Beneficiary created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all beneficiaries", description = "Retrieves all beneficiaries")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Beneficiaries retrieved successfully"
            )
    })
    public ResponseEntity<java.util.List<BeneficioResponse>> getAllBeneficios() {
        log.info("GET /api/beneficios - Fetching all beneficiaries");

        java.util.List<BeneficioResponse> responses = beneficioService.getAllBeneficios();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get beneficiary by ID", description = "Retrieves a beneficiary's information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Beneficiary found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BeneficioResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Beneficiary not found"
            )
    })
    public ResponseEntity<ApiResponse<BeneficioResponse>> getBeneficio(@PathVariable Long id) {
        log.info("GET /api/beneficios/{} - Fetching beneficiary", id);

        BeneficioResponse response = beneficioService.getBeneficio(id);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary found", response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between beneficiaries",
               description = "Transfers funds from one beneficiary to another with validation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid transfer parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Beneficiary not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "Insufficient balance"
            )
    })
    public ResponseEntity<ApiResponse<Void>> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("POST /api/beneficios/transfer - Executing transfer from {} to {} with amount {}",
                request.getFromBeneficioId(),
                request.getToBeneficioId(),
                request.getAmount());

        transferService.executeTransfer(request);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", null));
    }

    @ExceptionHandler(BeneficiaryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBeneficiaryNotFound(BeneficiaryNotFoundException e) {
        log.error("Beneficiary not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(InsufficientBalanceException e) {
        log.error("Insufficient balance: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
    }
}

