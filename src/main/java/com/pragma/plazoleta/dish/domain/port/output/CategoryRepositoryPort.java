package com.pragma.plazoleta.dish.domain.port.output;

import com.pragma.plazoleta.dish.domain.model.Category;

import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long categoryId);
}
