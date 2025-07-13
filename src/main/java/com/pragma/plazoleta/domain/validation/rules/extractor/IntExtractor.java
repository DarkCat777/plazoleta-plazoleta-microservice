package com.pragma.plazoleta.domain.validation.rules.extractor;

import java.util.function.Function;

@FunctionalInterface
public interface IntExtractor<T> extends Function<T, Integer> {
}