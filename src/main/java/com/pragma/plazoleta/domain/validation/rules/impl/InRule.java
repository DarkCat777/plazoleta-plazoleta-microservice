package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InRule<T, V> implements ValidationRule<T> {

    private final String field;
    private final Function<T, V> extractor;
    private final Collection<V> validValues;
    private final String message;

    private static final String DEFAULT_MESSAGE = "El valor debe ser uno de:";

    public InRule(String field, Function<T, V> extractor, Collection<V> validValues) {
        this(field, extractor, validValues, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        V value = extractor.apply(target);
        if (!validValues.contains(value)) {
            String validValuesStr = validValues.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return Optional.of(new FieldError(field, value, message + " " + validValuesStr));
        }
        return Optional.empty();
    }
}
