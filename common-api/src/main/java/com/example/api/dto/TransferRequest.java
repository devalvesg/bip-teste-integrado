package com.example.api.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private Long fromBeneficioId;
    private Long toBeneficioId;
    private BigDecimal amount;

    public TransferRequest() {
    }

    public TransferRequest(Long fromBeneficioId, Long toBeneficioId, BigDecimal amount) {
        this.fromBeneficioId = fromBeneficioId;
        this.toBeneficioId = toBeneficioId;
        this.amount = amount;
    }

    public Long getFromBeneficioId() {
        return fromBeneficioId;
    }

    public void setFromBeneficioId(Long fromBeneficioId) {
        this.fromBeneficioId = fromBeneficioId;
    }

    public Long getToBeneficioId() {
        return toBeneficioId;
    }

    public void setToBeneficioId(Long toBeneficioId) {
        this.toBeneficioId = toBeneficioId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromBeneficioId=" + fromBeneficioId +
                ", toBeneficioId=" + toBeneficioId +
                ", amount=" + amount +
                '}';
    }
}
