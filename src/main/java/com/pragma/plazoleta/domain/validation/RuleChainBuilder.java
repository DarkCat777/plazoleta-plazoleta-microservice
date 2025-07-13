package com.pragma.plazoleta.domain.validation;

import com.pragma.plazoleta.domain.validation.rules.extractor.IntExtractor;
import com.pragma.plazoleta.domain.validation.rules.extractor.LongExtractor;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import com.pragma.plazoleta.domain.validation.rules.extractor.StringExtractor;
import com.pragma.plazoleta.domain.validation.rules.impl.*;

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


    public RuleChainBuilder<T> pattern(String fieldName, StringExtractor<T> extractor, String pattern) {
        rules.add(new PatternRule<>(fieldName, extractor, pattern));
        return this;
    }

    public <R> RuleChainBuilder<T> nested(
                String fieldName,
                Function<T, R> extractor,
                Function<RuleChainBuilder<R>, List<ValidationRule<R>>> ruleFactory
        ) {
            RuleChainBuilder<R> builder = new RuleChainBuilder<>();
            List<ValidationRule<R>> rules = ruleFactory.apply(builder);
            this.rules.add(new NestedRule<>(fieldName, extractor, rules));
            return this;
        }

    public List<ValidationRule<T>> build() {
        return rules;
    }

}
