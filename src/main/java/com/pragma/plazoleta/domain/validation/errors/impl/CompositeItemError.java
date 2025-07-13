package com.pragma.plazoleta.domain.validation.errors.impl;

import com.pragma.plazoleta.domain.validation.errors.ValidationError;
import com.pragma.plazoleta.domain.validation.errors.WithChildrenError;
import com.pragma.plazoleta.domain.validation.errors.WithIndex;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CompositeItemError implements ValidationError, WithChildrenError, WithIndex {
    private final Integer index;
    private final Object rejectedValue;
    private final List<ValidationError> children;
}
