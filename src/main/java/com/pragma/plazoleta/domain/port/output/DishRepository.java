package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Dish;

public interface DishRepository {
    Dish save(Dish dish);
}