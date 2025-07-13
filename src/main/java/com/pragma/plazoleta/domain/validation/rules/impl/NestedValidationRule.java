package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.CompositeValidationError;
import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class NestedValidationRule<T, R> implements ValidationRule<T> {
    private final String fieldName;
    private final Function<T, R> extractor;
    private final List<ValidationRule<R>> nestedRules;

    @Override
    public Optional<FieldValidationError> validate(T object) {
        R value = extractor.apply(object);
        if (value == null) {
            return Optional.of(new FieldValidationError(fieldName, null, NotNullRule.DEFAULT_MESSAGE));
        }
        List<FieldValidationError> errors = new ArrayList<>();
        for (ValidationRule<R> rule : nestedRules) {
            rule.validate(value)
                    .ifPresent(error ->
                            errors.add(new FieldValidationError(error.getField(), value, error.getMessage()))
                    );
        }
        if (!errors.isEmpty()) {
            return Optional.of(new CompositeValidationError(fieldName, errors));
        }
        return Optional.empty();
    }
}
