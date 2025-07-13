package com.pragma.plazoleta.domain.validation;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import com.pragma.plazoleta.domain.validation.rules.extractor.*;
import com.pragma.plazoleta.domain.validation.rules.impl.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Validation<T> {

    private final T object;
    private final List<ValidationRule<T>> rules;

    public static <T> Builder<T> builder(T object) {
        return new Builder<>(object);
    }

    public void validate() throws ValidationException {
        List<ValidationError> errors = rules.stream()
                .map(rule -> rule.validate(object))
                .flatMap(Optional::stream)
                .toList();
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @AllArgsConstructor
    public static class Builder<T> {

        private final T object;
        private final List<ValidationRule<T>> rules = new ArrayList<>();

        public Builder<T> email(String fieldName, StringExtractor<T> extractor, String message) {
            rules.add(new EmailRule<>(fieldName, extractor, message));
            return this;
        }

        public Builder<T> email(String fieldName, StringExtractor<T> extractor) {
            rules.add(new EmailRule<>(fieldName, extractor));
            return this;
        }

        public Builder<T> minLength(String fieldName, StringExtractor<T> extractor, Integer min, String message) {
            rules.add(new MinLengthRule<>(fieldName, extractor, min, message));
            return this;
        }

        public Builder<T> minLength(String fieldName, StringExtractor<T> extractor, Integer min) {
            rules.add(new MinLengthRule<>(fieldName, extractor, min));
            return this;
        }

        public Builder<T> notBlank(String fieldName, StringExtractor<T> extractor, String message) {
            rules.add(new NotBlankRule<>(fieldName, extractor, message));
            return this;
        }

        public Builder<T> notBlank(String fieldName, StringExtractor<T> extractor) {
            rules.add(new NotBlankRule<>(fieldName, extractor));
            return this;
        }

        public Builder<T> notNull(String fieldName, Function<T, ?> extractor, String message) {
            rules.add(new NotNullRule<>(fieldName, extractor, message));
            return this;
        }

        public Builder<T> notNull(String fieldName, Function<T, ?> extractor) {
            rules.add(new NotNullRule<>(fieldName, extractor));
            return this;
        }

        public Builder<T> notEmpty(String fieldName, Function<T, List<?>> extractor) {
            rules.add(new NotEmptyRule<>(fieldName, extractor));
            return this;
        }

        public Builder<T> pattern(String fieldName, StringExtractor<T> extractor, String pattern, String message) {
            rules.add(new PatternRule<>(fieldName, extractor, pattern, message));
            return this;
        }

        public Builder<T> pattern(String fieldName, StringExtractor<T> extractor, String pattern) {
            rules.add(new PatternRule<>(fieldName, extractor, pattern));
            return this;
        }

        // --- BigDecimal ---
        public Builder<T> positive(String fieldName, BigDecimalExtractor<T> extractor, String message) {
            rules.add(new PositiveRule<>(fieldName, extractor, message));
            return this;
        }

        public Builder<T> positive(String fieldName, BigDecimalExtractor<T> extractor) {
            rules.add(new PositiveRule<>(fieldName, extractor));
            return this;
        }

        // --- Integer ---
        public Builder<T> positive(String fieldName, IntExtractor<T> extractor, String message) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Integer value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }, message));
            return this;
        }

        public Builder<T> positive(String fieldName, IntExtractor<T> extractor) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Integer value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }));
            return this;
        }

        // --- Long ---
        public Builder<T> positive(String fieldName, LongExtractor<T> extractor, String message) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Long value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }, message));
            return this;
        }

        public Builder<T> positive(String fieldName, LongExtractor<T> extractor) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Long value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }));
            return this;
        }

        // --- Double ---
        public Builder<T> positive(String fieldName, DoubleExtractor<T> extractor, String message) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Double value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }, message));
            return this;
        }

        public Builder<T> positive(String fieldName, DoubleExtractor<T> extractor) {
            rules.add(new PositiveRule<>(fieldName, t -> {
                Double value = extractor.apply(t);
                return value != null ? BigDecimal.valueOf(value) : null;
            }));
            return this;
        }

        public Builder<T> minYearDifference(String field, Function<T, LocalDate> extractor, int minYears, String message) {
            rules.add(new MinYearDifferenceRule<>(field, t -> LocalDate.now(), extractor, minYears, message));
            return this;
        }

        public Builder<T> minYearDifference(String field, Function<T, LocalDate> extractor, int minYears) {
            rules.add(new MinYearDifferenceRule<>(field, t -> LocalDate.now(), extractor, minYears));
            return this;
        }

        public Builder<T> notMatch(String field, Function<T, String> extractor, String pattern) {
            rules.add(new NotPatternRule<>(field, extractor, pattern));
            return this;
        }

        public <E> Builder<T> each(
                String fieldName,
                Function<T, List<E>> extractor,
                Function<RuleChainBuilder<E>, List<ValidationRule<E>>> ruleFactory
        ) {
            RuleChainBuilder<E> ruleBuilder = new RuleChainBuilder<>();
            List<ValidationRule<E>> elementRules = ruleFactory.apply(ruleBuilder);
            rules.add(new EachElementRule<>(fieldName, extractor, elementRules));
            return this;
        }

        public <R> Builder<T> nested(
                String fieldName,
                Function<T, R> extractor,
                Function<RuleChainBuilder<R>, List<ValidationRule<R>>> ruleFactory
        ) {
            RuleChainBuilder<R> builder = new RuleChainBuilder<>();
            List<ValidationRule<R>> rules = ruleFactory.apply(builder);
            this.rules.add(new NestedRule<>(fieldName, extractor, rules));
            return this;
        }

        public Validation<T> build() {
            return new Validation<>(object, rules);
        }

    }

}
