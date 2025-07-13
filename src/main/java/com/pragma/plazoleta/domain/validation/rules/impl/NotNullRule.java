package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class NotNullRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, ?> extractor;
    private final String message;

    public static final String DEFAULT_MESSAGE = "No debe ser nulo.";

    public NotNullRule(String fieldName, Function<T, ?> extractor) {
        this(fieldName, extractor, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        Object value = extractor.apply(target);
        if (value == null)
            return Optional.of(new FieldError(fieldName, null, MessageFormat.format(message, value)));
        return Optional.empty();
    }
}