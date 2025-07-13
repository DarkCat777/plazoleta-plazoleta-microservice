package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class NotEmptyRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, List<?>> extractor;
    private final String message;

    public static final String DEFAULT_MESSAGE = "La lista no puede estar vacía.";

    public NotEmptyRule(String field, Function<T, List<?>> extractor) {
        this(field, extractor, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        List<?> value = extractor.apply(target);
        if (value == null || value.isEmpty()) {
            return Optional.of(new FieldError(fieldName, value, message));
        }
        return Optional.empty();
    }
}
