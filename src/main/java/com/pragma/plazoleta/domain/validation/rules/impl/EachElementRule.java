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
public class EachElementRule<T, E> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, List<E>> extractor;
    private final List<ValidationRule<E>> rules;

    @Override
    public Optional<FieldValidationError> validate(T target) {
        List<E> elements = extractor.apply(target);
        if (elements == null) {
            return Optional.empty();
        }
        List<FieldValidationError> errors = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            E item = elements.get(i);
            for (ValidationRule<E> rule : rules) {
                Optional<FieldValidationError> error = rule.validate(item);
                if (error.isPresent()) {
                    errors.add(new FieldValidationError(
                            "[" + i + "]." + error.get().getField(),
                            elements.get(i),
                            error.get().getMessage()
                    ));
                }
            }
        }
        if (!errors.isEmpty()) {
            return Optional.of(new CompositeValidationError(fieldName, errors));
        }
        return Optional.empty();
    }
}
