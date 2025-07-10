package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.dish.application.port.input.GetPagedDishByRestaurantAndCategoryUseCase;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetPagedDishByRestaurantAndCategoryUseCaseImpl implements GetPagedDishByRestaurantAndCategoryUseCase {

    private final DishRepositoryPort dishRepositoryPort;

    @Override
    public PaginationResult<Dish> execute(Long restaurantId, Long categoryId, PaginationQuery paginationQuery) {
        return dishRepositoryPort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery);
    }
}
