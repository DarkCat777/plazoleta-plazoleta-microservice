package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Dish;

public interface DishUseCase {

    Dish createDish(Long ownerId, Dish command);

    Dish updateDish(Long ownerId, Long dishId, Dish command);

    Dish updateDishStatus(Long ownerId, Long dishId, Dish command);

    PaginationResult<Dish> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery);

}
