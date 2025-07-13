package com.pragma.plazoleta.domain.validation.rules.extractor;

import java.util.function.Function;

@FunctionalInterface
public interface StringExtractor<T> extends Function<T, String> {
}
