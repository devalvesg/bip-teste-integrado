package com.example.backend.application.service;

import com.example.api.dto.TransferRequest;
import com.example.api.service.ITransferOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {

    private final ITransferOperationService ITransferOperationService;

    public void executeTransfer(TransferRequest request) {
        log.info("Executing transfer from beneficiary {} to {} with amount {}",
                request.getFromBeneficioId(),
                request.getToBeneficioId(),
                request.getAmount());

        ITransferOperationService.transfer(
                request.getFromBeneficioId(),
                request.getToBeneficioId(),
                request.getAmount()
        );

        log.info("Transfer completed successfully");
    }
}



