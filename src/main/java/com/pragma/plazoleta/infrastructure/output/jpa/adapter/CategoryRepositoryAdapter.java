package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.persistence.CategoryRepositoryPort;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.CategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository categoryRepository;
    private final CategoryEntityMapper categoryMapper;

    @Override
    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId).map(categoryMapper::toDomain);
    }
}
