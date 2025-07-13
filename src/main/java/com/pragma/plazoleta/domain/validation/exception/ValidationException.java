package com.pragma.plazoleta.domain.validation.exception;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;
}