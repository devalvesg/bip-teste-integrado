package com.example.api.service;

import java.math.BigDecimal;

public interface ITransferOperationService {

    void transfer(Long fromId, Long toId, BigDecimal amount);
}
