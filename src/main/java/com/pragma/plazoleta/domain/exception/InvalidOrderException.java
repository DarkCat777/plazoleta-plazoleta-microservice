package com.pragma.plazoleta.domain.exception;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String s) {
        super(s);
    }
}
