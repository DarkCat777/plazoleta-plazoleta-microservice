package com.pragma.plazoleta.dish.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.dish.infrastructure.adapter.output.repository.JpaDishRepository;
import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.dish.infrastructure.adapter.mapper.DishEntityMapper;
import com.pragma.plazoleta.shared.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.shared.mapper.PaginationResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishRepositoryAdapter implements DishRepositoryPort {

    private final JpaDishRepository repository;
    private final DishEntityMapper entityMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public Dish save(Dish dish) {
        return entityMapper.toDomain(repository.save(entityMapper.toEntity(dish)));
    }

    @Override
    public Optional<Dish> findById(Long dishId) {
        return repository.findById(dishId).map(entityMapper::toDomain);
    }

    @Override
    public PaginationResult<Dish> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery) {
        Pageable pageable = paginationQueryMapper.toPageable(paginationQuery);
        Page<Dish> page = repository.findAllByRestaurant_IdAndCategory_Id(restaurantId, categoryId, pageable).map(entityMapper::toDomain);
        return paginationResultMapper.toPaginatedResult(page);
    }
}
