package com.pragma.plazoleta.domain.validation.extractor;

import java.util.function.Function;

@FunctionalInterface
public interface StringExtractor<T> extends Function<T, String> {
}
