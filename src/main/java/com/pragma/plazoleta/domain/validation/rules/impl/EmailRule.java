package com.pragma.plazoleta.domain.validation.rules.impl;


import java.util.function.Function;

public class EmailRule<T> extends PatternRule<T> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String DEFAULT_MESSAGE = "Debe ser un correo valido.";

    public EmailRule(String fieldName, Function<T, String> extractor, String message) {
        super(fieldName, extractor, EMAIL_REGEX, message);
    }

    public EmailRule(String fieldName, Function<T, String> extractor) {
        this(fieldName, extractor, DEFAULT_MESSAGE);
    }
}
