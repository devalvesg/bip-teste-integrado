package com.example.ejb;

import com.example.api.entity.Beneficio;
import com.example.api.exception.BeneficiaryNotFoundException;
import com.example.api.exception.InsufficientBalanceException;
import com.example.ejb.service.BeneficioEjbService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BeneficioEjbService Unit Tests")
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService beneficioEjbService;

    private Beneficio fromBeneficio;
    private Beneficio toBeneficio;

    @BeforeEach
    void setUp() {
        fromBeneficio = new Beneficio("John Doe", new BigDecimal("1000.00"));
        fromBeneficio.setId(1L);
        fromBeneficio.setVersion(0L);

        toBeneficio = new Beneficio("Jane Doe", new BigDecimal("500.00"));
        toBeneficio.setId(2L);
        toBeneficio.setVersion(0L);
    }

    @Test
    @DisplayName("Should transfer successfully with sufficient balance")
    void testTransferSuccess() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");

        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(fromBeneficio);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(toBeneficio);
        when(em.merge(fromBeneficio)).thenReturn(fromBeneficio);
        when(em.merge(toBeneficio)).thenReturn(toBeneficio);

        // Act
        assertDoesNotThrow(() -> beneficioEjbService.transfer(1L, 2L, transferAmount));

        // Assert
        assertEquals(new BigDecimal("900.00"), fromBeneficio.getBalance());
        assertEquals(new BigDecimal("600.00"), toBeneficio.getBalance());

        verify(em, times(2)).merge(any(Beneficio.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance is insufficient")
    void testTransferInsufficientBalance() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("2000.00");

        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(fromBeneficio);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(toBeneficio);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> beneficioEjbService.transfer(1L, 2L, transferAmount)
        );

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("Should throw BeneficiaryNotFoundException when source not found")
    void testTransferSourceNotFound() {
        // Arrange
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(null);

        // Act & Assert
        BeneficiaryNotFoundException exception = assertThrows(
                BeneficiaryNotFoundException.class,
                () -> beneficioEjbService.transfer(1L, 2L, new BigDecimal("100.00"))
        );

        assertTrue(exception.getMessage().contains("Source beneficiary"));
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("Should throw BeneficiaryNotFoundException when destination not found")
    void testTransferDestinationNotFound() {
        // Arrange
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(fromBeneficio);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(null);

        // Act & Assert
        BeneficiaryNotFoundException exception = assertThrows(
                BeneficiaryNotFoundException.class,
                () -> beneficioEjbService.transfer(1L, 2L, new BigDecimal("100.00"))
        );

        assertTrue(exception.getMessage().contains("Destination beneficiary"));
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when IDs are null")
    void testTransferNullIds() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(null, 2L, new BigDecimal("100.00"))
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(1L, null, new BigDecimal("100.00"))
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is null")
    void testTransferNullAmount() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(1L, 2L, null)
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is zero or negative")
    void testTransferInvalidAmount() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(1L, 2L, BigDecimal.ZERO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(1L, 2L, new BigDecimal("-100.00"))
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when source and destination are the same")
    void testTransferSameBeneficiary() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> beneficioEjbService.transfer(1L, 1L, new BigDecimal("100.00"))
        );
    }
}

