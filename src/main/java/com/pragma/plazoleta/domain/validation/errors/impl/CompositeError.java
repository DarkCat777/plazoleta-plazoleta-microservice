package com.pragma.plazoleta.domain.validation.errors.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.WithChildrenError;
import com.pragma.plazoleta.domain.validation.errors.WithField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CompositeError implements ValidationError, WithField, WithChildrenError {

    private final String field;

    private final Object rejectedValue;

    private final List<ValidationError> children;

}
