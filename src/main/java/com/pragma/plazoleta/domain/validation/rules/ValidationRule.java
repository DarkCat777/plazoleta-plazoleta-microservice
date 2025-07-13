package com.pragma.plazoleta.domain.validation.rules;

import com.pragma.plazoleta.domain.validation.FieldValidationError;

import java.util.Optional;

public interface ValidationRule<T> {
    Optional<FieldValidationError> validate(T target);
}
