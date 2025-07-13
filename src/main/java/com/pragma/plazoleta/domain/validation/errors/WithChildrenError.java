package com.pragma.plazoleta.domain.validation.errors;

import java.util.List;

public interface WithChildrenError {

    List<ValidationError> getChildren();

}
