package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Category;

import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(Long categoryId);
}
