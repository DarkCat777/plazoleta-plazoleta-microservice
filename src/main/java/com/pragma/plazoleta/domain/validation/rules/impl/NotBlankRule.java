package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class NotBlankRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, String> extractor;
    private final String message;

    public static final String DEFAULT_MESSAGE = "No debe estar vacío.";

    public NotBlankRule(String fieldName, Function<T, String> extractor) {
        this(fieldName, extractor, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        String value = extractor.apply(target);
        if (value == null || value.isBlank())
            return Optional.of(new FieldError(fieldName, value, String.format(message, value)));
        return Optional.empty();
    }
}