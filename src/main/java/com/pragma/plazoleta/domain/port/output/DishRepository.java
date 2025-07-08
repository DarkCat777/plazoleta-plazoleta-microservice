package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Dish;

import java.util.Optional;

public interface DishRepository {
    Dish save(Dish dish);

    Optional<Dish> findById(Long restaurantId);
}