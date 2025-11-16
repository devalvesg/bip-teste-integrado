package com.example.api.exception;

public class BeneficiaryNotFoundException extends RuntimeException {

    public BeneficiaryNotFoundException(String message) {
        super(message);
    }

    public BeneficiaryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
