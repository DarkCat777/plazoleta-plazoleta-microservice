package com.pragma.plazoleta.auth.application.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}