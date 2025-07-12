package com.pragma.plazoleta.domain.spi.persistence;

import com.pragma.plazoleta.domain.model.Category;

import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long categoryId);
}
