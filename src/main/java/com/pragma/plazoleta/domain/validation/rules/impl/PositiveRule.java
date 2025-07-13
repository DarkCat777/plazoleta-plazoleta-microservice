package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class PositiveRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, BigDecimal> extractor;
    private final String message;

    public static String DEFAULT_MESSAGE = "Debe ser mayor a cero.";

    public PositiveRule(String fieldName, Function<T, BigDecimal> extractor) {
        this(fieldName, extractor, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<FieldValidationError> validate(T target) {
        BigDecimal value = extractor.apply(target);
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of(new FieldValidationError(fieldName, value, MessageFormat.format(message, value)));
        }
        return Optional.empty();
    }
}