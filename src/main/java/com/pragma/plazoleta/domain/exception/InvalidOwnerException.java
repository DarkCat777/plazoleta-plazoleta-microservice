package com.pragma.plazoleta.domain.exception;

public class InvalidOwnerException extends RuntimeException {
    public InvalidOwnerException(String message) {
        super(message);
    }
}
