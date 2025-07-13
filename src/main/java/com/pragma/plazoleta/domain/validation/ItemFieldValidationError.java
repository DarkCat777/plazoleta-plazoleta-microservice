package com.pragma.plazoleta.domain.validation;

import lombok.Getter;

@Getter
public class ItemFieldValidationError extends FieldValidationError {

    private final Integer index;

    public ItemFieldValidationError(Integer index, String field, Object rejectedValue, String message) {
        super(field, rejectedValue, message);
        this.index = index;
    }
}
