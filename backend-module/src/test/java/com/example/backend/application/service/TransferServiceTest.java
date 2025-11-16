package com.example.backend.application.service;

import com.example.api.dto.TransferRequest;
import com.example.api.exception.BeneficiaryNotFoundException;
import com.example.api.exception.InsufficientBalanceException;
import com.example.api.service.ITransferOperationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferService Tests")
class TransferServiceTest {

    @Mock
    private ITransferOperationService ITransferOperationService;

    @InjectMocks
    private TransferService transferService;

    @Test
    @DisplayName("Should execute transfer successfully when calling EJB service")
    void testExecuteTransferSuccess() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(1L);
        request.setToBeneficioId(2L);
        request.setAmount(new BigDecimal("100.00"));

        assertDoesNotThrow(() -> transferService.executeTransfer(request));

        verify(ITransferOperationService).transfer(1L, 2L, new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should throw BeneficioNotFoundException when source beneficiary not found")
    void testExecuteTransferSourceNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(999L);
        request.setToBeneficioId(2L);
        request.setAmount(new BigDecimal("100.00"));

        doThrow(new BeneficiaryNotFoundException("Source beneficiary not found"))
                .when(ITransferOperationService).transfer(999L, 2L, new BigDecimal("100.00"));

        assertThrows(BeneficiaryNotFoundException.class, () -> transferService.executeTransfer(request));
    }

    @Test
    @DisplayName("Should throw BeneficioNotFoundException when destination beneficiary not found")
    void testExecuteTransferDestinationNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(1L);
        request.setToBeneficioId(999L);
        request.setAmount(new BigDecimal("100.00"));

        doThrow(new BeneficiaryNotFoundException("Destination beneficiary not found"))
                .when(ITransferOperationService).transfer(1L, 999L, new BigDecimal("100.00"));

        assertThrows(BeneficiaryNotFoundException.class, () -> transferService.executeTransfer(request));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance is insufficient")
    void testExecuteTransferInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(1L);
        request.setToBeneficioId(2L);
        request.setAmount(new BigDecimal("10000.00"));

        doThrow(new InsufficientBalanceException("Insufficient balance"))
                .when(ITransferOperationService).transfer(1L, 2L, new BigDecimal("10000.00"));

        assertThrows(InsufficientBalanceException.class, () -> transferService.executeTransfer(request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when IDs are null")
    void testExecuteTransferNullIds() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(null);
        request.setToBeneficioId(2L);
        request.setAmount(new BigDecimal("100.00"));

        doThrow(new IllegalArgumentException("IDs and amount cannot be null"))
                .when(ITransferOperationService).transfer(null, 2L, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> transferService.executeTransfer(request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is zero")
    void testExecuteTransferZeroAmount() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(1L);
        request.setToBeneficioId(2L);
        request.setAmount(BigDecimal.ZERO);

        doThrow(new IllegalArgumentException("Amount must be greater than zero"))
                .when(ITransferOperationService).transfer(1L, 2L, BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> transferService.executeTransfer(request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when source and destination are the same")
    void testExecuteTransferSameIds() {
        TransferRequest request = new TransferRequest();
        request.setFromBeneficioId(1L);
        request.setToBeneficioId(1L);
        request.setAmount(new BigDecimal("100.00"));

        doThrow(new IllegalArgumentException("Source and destination beneficiaries must be different"))
                .when(ITransferOperationService).transfer(1L, 1L, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> transferService.executeTransfer(request));
    }
}