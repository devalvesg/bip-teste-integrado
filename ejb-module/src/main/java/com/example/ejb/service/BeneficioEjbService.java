package com.example.ejb.service;

import com.example.api.entity.Beneficio;
import com.example.api.exception.BeneficiaryNotFoundException;
import com.example.api.exception.InsufficientBalanceException;
import com.example.api.service.ITransferOperationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class BeneficioEjbService implements ITransferOperationService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        log.debug("Starting transfer: from={}, to={}, amount={}", fromId, toId, amount);

        validateInputParameters(fromId, toId, amount);

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        if (from == null) {
            log.warn("Source beneficiary not found: {}", fromId);
            throw new BeneficiaryNotFoundException(
                    "Source beneficiary with ID " + fromId + " not found"
            );
        }

        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);
        if (to == null) {
            log.warn("Destination beneficiary not found: {}", toId);
            throw new BeneficiaryNotFoundException(
                    "Destination beneficiary with ID " + toId + " not found"
            );
        }

        validateSufficientBalance(from, amount);

        BigDecimal newFromBalance = from.getBalance().subtract(amount);
        BigDecimal newToBalance = to.getBalance().add(amount);

        from.setBalance(newFromBalance);
        to.setBalance(newToBalance);

        log.info("Transfer executed: from ID {} balance {} -> {}, to ID {} balance {} -> {}",
                fromId, from.getBalance().add(amount), newFromBalance,
                toId, to.getBalance().subtract(amount), newToBalance);

        em.merge(from);
        em.merge(to);

        log.debug("Transfer completed successfully: from={}, to={}, amount={}",
                fromId, toId, amount);
    }

    private void validateInputParameters(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null || amount == null) {
            throw new IllegalArgumentException(
                    "IDs and amount cannot be null"
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero, got: " + amount
            );
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Source and destination beneficiaries must be different"
            );
        }
    }

    private void validateSufficientBalance(Beneficio from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance for beneficiary {}: current={}, requested={}",
                    from.getId(), from.getBalance(), amount);
            throw new InsufficientBalanceException(
                    "Insufficient balance. Current balance: " + from.getBalance() +
                    ", Requested transfer: " + amount
            );
        }
    }
}
