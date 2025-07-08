package com.pragma.plazoleta.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.port.output.CategoryRepository;
import com.pragma.plazoleta.infrastructure.adapter.mapper.CategoryMapper;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId).map(categoryMapper::toDomain);
    }
}
