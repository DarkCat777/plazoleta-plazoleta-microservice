package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class MinLengthRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, String> extractor;
    private final Integer min;
    private final String message;

    public static final String DEFAULT_MESSAGE = "Debe tener la longitud minima de {0}.";

    public MinLengthRule(String fieldName, Function<T, String> extractor, Integer min) {
        this(fieldName, extractor, min, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<FieldValidationError> validate(T target) {
        String value = extractor.apply(target);
        if (value == null || value.length() < min) {
            return Optional.of(new FieldValidationError(fieldName, value, String.format(message, min, value)));
        }
        return Optional.empty();
    }
}
