package com.pragma.plazoleta.domain.exception;

import com.pragma.plazoleta.domain.validation.FieldValidationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {
    private final List<FieldValidationError> errors;
}