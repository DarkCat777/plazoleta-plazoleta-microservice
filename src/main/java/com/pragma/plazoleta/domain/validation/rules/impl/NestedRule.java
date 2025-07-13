package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.CompositeError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class NestedRule<T, V> implements ValidationRule<T> {
    private final String fieldName;
    private final Function<T, V> extractor;
    private final List<ValidationRule<V>> rules;

    @Override
    public Optional<ValidationError> validate(T target) {
        V value = extractor.apply(target);
        if (value == null) {
            return Optional.of(new FieldError(fieldName, null, "No debe ser nulo."));
        }

        List<ValidationError> childErrors = new ArrayList<>();
        for (ValidationRule<V> rule : rules) {
            rule.validate(value).ifPresent(childErrors::add);
        }

        if (!childErrors.isEmpty()) {
            return Optional.of(new CompositeError(fieldName, value, childErrors));
        }

        return Optional.empty();
    }
}

