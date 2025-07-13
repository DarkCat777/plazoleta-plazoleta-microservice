package com.pragma.plazoleta.domain.spi.persistence;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Dish;

import java.util.List;
import java.util.Optional;

public interface DishRepositoryPort {
    Dish save(Dish dish);

    Optional<Dish> findById(Long dishId);

    PaginationResult<Dish> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery);

    List<Dish> findAllById(List<Long> list);
}