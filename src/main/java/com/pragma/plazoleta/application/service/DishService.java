package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.request.CreateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DishService {

    DishResponse createDish(Long ownerId, CreateDishCommand command);

    DishResponse updateDish(Long ownerId, Long dishId, UpdateDishCommand command);

    DishResponse updateDishStatus(Long ownerId, Long dishId, UpdateStatusDishCommand command);

    Page<DishResponse> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, Pageable pageable);

}
