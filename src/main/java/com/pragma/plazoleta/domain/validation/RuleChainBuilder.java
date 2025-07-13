package com.pragma.plazoleta.domain.validation;

import com.pragma.plazoleta.domain.validation.extractor.IntExtractor;
import com.pragma.plazoleta.domain.validation.extractor.LongExtractor;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import com.pragma.plazoleta.domain.validation.rules.impl.NotBlankRule;
import com.pragma.plazoleta.domain.validation.rules.impl.NotNullRule;
import com.pragma.plazoleta.domain.validation.rules.impl.PositiveRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RuleChainBuilder<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    public RuleChainBuilder<T> notNull(String field, Function<T, ?> extractor) {
        rules.add(new NotNullRule<>(field, extractor));
        return this;
    }

    public RuleChainBuilder<T> notBlank(String field, Function<T, String> extractor) {
        rules.add(new NotBlankRule<>(field, extractor));
        return this;
    }

    public RuleChainBuilder<T> positive(String fieldName, LongExtractor<T> extractor) {
        rules.add(new PositiveRule<>(fieldName, t -> {
            Long value = extractor.apply(t);
            return value != null ? BigDecimal.valueOf(value) : null;
        }));
        return this;
    }

    public RuleChainBuilder<T> positive(String fieldName, IntExtractor<T> extractor) {
        rules.add(new PositiveRule<>(fieldName, t -> {
            Integer value = extractor.apply(t);
            return value != null ? BigDecimal.valueOf(value) : null;
        }));
        return this;
    }

    public List<ValidationRule<T>> build() {
        return rules;
    }

}
