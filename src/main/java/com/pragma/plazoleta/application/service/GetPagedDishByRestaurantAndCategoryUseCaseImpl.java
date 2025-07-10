package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.port.input.GetPagedDishByRestaurantAndCategoryUseCase;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.port.output.DishRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetPagedDishByRestaurantAndCategoryUseCaseImpl implements GetPagedDishByRestaurantAndCategoryUseCase {

    private final DishRepositoryPort dishRepositoryPort;

    @Override
    public PaginationResult<Dish> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery) {
        return dishRepositoryPort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery);
    }
}
