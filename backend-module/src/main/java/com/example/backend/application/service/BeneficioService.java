package com.example.backend.application.service;

import com.example.api.entity.Beneficio;
import com.example.api.exception.BeneficiaryNotFoundException;
import com.example.backend.application.dto.BeneficioResponse;
import com.example.backend.application.dto.CreateBeneficioRequest;
import com.example.backend.infrastructure.persistence.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;

    public BeneficioResponse createBeneficio(CreateBeneficioRequest request) {
        log.info("Creating new beneficiary with name: {}", request.getName());

        Beneficio beneficio = new Beneficio(request.getName(), request.getBalance());
        Beneficio saved = beneficioRepository.save(beneficio);

        log.info("Beneficiary created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public BeneficioResponse getBeneficio(Long id) {
        log.info("Fetching beneficiary with ID: {}", id);

        Beneficio beneficio = beneficioRepository.findById(id)
                .orElseThrow(() -> new BeneficiaryNotFoundException(
                        "Beneficiary with ID " + id + " not found"
                ));

        return mapToResponse(beneficio);
    }

    private BeneficioResponse mapToResponse(Beneficio beneficio) {
        return new BeneficioResponse(
                beneficio.getId(),
                beneficio.getName(),
                beneficio.getBalance(),
                beneficio.getVersion()
        );
    }
}

