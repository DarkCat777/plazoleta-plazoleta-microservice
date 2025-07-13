package com.pragma.plazoleta.domain.validation.extractor;


import java.math.BigDecimal;
import java.util.function.Function;

@FunctionalInterface
public interface BigDecimalExtractor<T> extends Function<T, BigDecimal> {
}