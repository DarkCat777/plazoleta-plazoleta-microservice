package com.pragma.plazoleta.domain.validation;

import lombok.Getter;

import java.util.List;

@Getter
public class CompositeValidationError extends FieldValidationError {

    private final List<FieldValidationError> children;

    public CompositeValidationError(String field, List<FieldValidationError> children) {
        super(field, null, null);
        this.children = children;
    }
}
