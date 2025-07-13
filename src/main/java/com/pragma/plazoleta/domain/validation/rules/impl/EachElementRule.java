package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.CompositeError;
import com.pragma.plazoleta.domain.validation.errors.impl.CompositeItemError;
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
    public Optional<ValidationError> validate(T target) {
        List<E> elements = extractor.apply(target);
        if (elements == null) {
            return Optional.empty();
        }

        List<ValidationError> indexedErrors = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
            E item = elements.get(i);
            List<ValidationError> itemErrors = new ArrayList<>();

            for (ValidationRule<E> rule : rules) {
                rule.validate(item).ifPresent(itemErrors::add);
            }

            if (!itemErrors.isEmpty()) {
                indexedErrors.add(new CompositeItemError(i, item, itemErrors));
            }
        }

        if (!indexedErrors.isEmpty()) {
            return Optional.of(new CompositeError(fieldName, elements, indexedErrors));
        }

        return Optional.empty();
    }
}
