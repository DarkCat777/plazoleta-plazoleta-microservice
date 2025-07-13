package com.pragma.plazoleta.domain.validation.rules.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class MinYearDifferenceRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, LocalDate> referenceExtractor;
    private final Function<T, LocalDate> extractor;
    private final int minYears;
    private final String message;

    public static final String DEFAULT_MESSAGE = "La diferencia entre años debe de ser de al menos {0} años.";

    public MinYearDifferenceRule(String fieldName, Function<T, LocalDate> referenceExtractor, Function<T, LocalDate> extractor, int minYears) {
        this(fieldName, referenceExtractor, extractor, minYears, DEFAULT_MESSAGE);
    }

    @Override
    public Optional<ValidationError> validate(T target) {
        LocalDate date = extractor.apply(target);
        LocalDate fromDate = referenceExtractor.apply(target);
        if (date == null || Period.between(date, fromDate).getYears() < minYears) {
            return Optional.of(new FieldError(fieldName, date, MessageFormat.format(message, minYears)));
        }
        return Optional.empty();
    }
}
