package com.pragma.plazoleta.domain.validation;

import java.text.MessageFormat;

public class ValidationUtils {

    public static final String NOT_BLANK = "El campo {0} no debe estar vacío";
    public static final String NOT_NULL = "El campo {0} no debe ser nulo";
    public static final String POSITIVE = "El campo {0} debe ser un número positivo";
    public static final String ONLY_NUMBERS = "El campo {0} debe contener solo números";
    public static final String ONLY_NON_NUMERIC = "El campo {0} no puede contener solo números";
    public static final String PHONE_FORMAT = "Teléfono inválido (solo números y opcional +, máx. 13 dígitos)";

    public static FieldValidationError validateNotBlank(String field, String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationError(
                    field,
                    value,
                    MessageFormat.format(ValidationUtils.NOT_BLANK, field)
            );
        }
        return null;
    }

    public static FieldValidationError validateNotNull(String field, Object value) {
        if (value == null) {
            return new FieldValidationError(
                    field,
                    null,
                    MessageFormat.format(ValidationUtils.NOT_NULL, field)
            );
        }
        return null;
    }

    public static FieldValidationError validatePositive(String field, Number value) {
        if (value == null || value.doubleValue() <= 0) {
            return new FieldValidationError(
                    field,
                    value,
                    MessageFormat.format(ValidationUtils.POSITIVE, field)
            );
        }
        return null;
    }

    public static FieldValidationError validateOnlyNumbers(String field, String value) {
        if (value == null || !value.matches("^\\d+$")) {
            return new FieldValidationError(
                    field,
                    value,
                    MessageFormat.format(ONLY_NUMBERS, field)
            );
        }
        return null;
    }

    public static FieldValidationError validateNotOnlyNumbers(String field, String value) {
        if (value == null || value.matches("^\\d+$")) {
            return new FieldValidationError(
                    field,
                    value,
                    MessageFormat.format(ONLY_NON_NUMERIC, field)
            );
        }
        return null;
    }

    public static FieldValidationError validatePhone(String field, String value) {
        if (value == null || !value.matches("^\\+?\\d{1,13}$")) {
            return new FieldValidationError(
                    field,
                    value,
                    PHONE_FORMAT
            );
        }
        return null;
    }


    private ValidationUtils() {
    }
}