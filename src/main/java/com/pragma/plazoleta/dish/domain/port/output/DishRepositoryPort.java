package com.pragma.plazoleta.dish.domain.port.output;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.dish.domain.model.Dish;

import java.util.Optional;

public interface DishRepositoryPort {
    Dish save(Dish dish);

    Optional<Dish> findById(Long dishId);

    PaginationResult<Dish> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery);
}