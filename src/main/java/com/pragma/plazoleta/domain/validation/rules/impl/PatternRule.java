package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class PatternRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, String> extractor;
    private final String pattern;
    private final String message;

    public static final String DEFAULT_MESSAGE = "Debe seguir el siguiente patron {0}.";

    public PatternRule(String fieldName, Function<T, String> extractor, String pattern) {
        this(fieldName, extractor, pattern, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        String value = extractor.apply(target);
        if (value == null || !value.matches(pattern)) {
            return Optional.of(new FieldError(fieldName, value, MessageFormat.format(message, pattern, value)));
        }
        return Optional.empty();
    }
}
