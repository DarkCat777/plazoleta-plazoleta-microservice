package com.pragma.plazoleta.domain.validation.rules;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;

import java.util.Optional;

public interface ValidationRule<T> {
    Optional<ValidationError> validate(T target);
}
