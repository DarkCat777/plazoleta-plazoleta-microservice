package com.pragma.plazoleta.dish.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.dish.domain.model.Category;
import com.pragma.plazoleta.dish.domain.port.output.CategoryRepositoryPort;
import com.pragma.plazoleta.dish.infrastructure.adapter.mapper.CategoryEntityMapper;
import com.pragma.plazoleta.dish.infrastructure.adapter.output.repository.JpaCategoryRepository;
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
