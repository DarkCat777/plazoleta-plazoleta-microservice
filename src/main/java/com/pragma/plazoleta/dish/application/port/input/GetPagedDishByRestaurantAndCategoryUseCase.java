package com.pragma.plazoleta.dish.application.port.input;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.dish.domain.model.Dish;

public interface GetPagedDishByRestaurantAndCategoryUseCase {
    PaginationResult<Dish> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery);
}
