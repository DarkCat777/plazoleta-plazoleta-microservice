package com.pragma.plazoleta.domain.validation.rules.extractor;

import java.util.function.Function;

@FunctionalInterface
public interface DoubleExtractor<T> extends Function<T, Double> {
}
