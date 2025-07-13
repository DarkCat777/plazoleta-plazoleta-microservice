package com.pragma.plazoleta.domain.validation.rules.extractor;

import java.util.function.Function;

@FunctionalInterface
public interface LongExtractor<T> extends Function<T, Long> {
}
