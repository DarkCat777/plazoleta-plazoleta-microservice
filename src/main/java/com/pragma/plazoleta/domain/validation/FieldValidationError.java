package com.pragma.plazoleta.domain.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldValidationError {
    private final String field;
    private final Object rejectedValue;
    private final String message;
}
