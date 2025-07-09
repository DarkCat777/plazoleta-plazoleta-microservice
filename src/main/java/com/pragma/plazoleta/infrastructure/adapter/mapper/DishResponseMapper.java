package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.DishResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryResponseMapper.class})
public interface DishResponseMapper extends BaseResponseMapper<Dish, DishResponse> {

    @Override
    @Mapping(source = "restaurant.id", target = "restaurantId")
    DishResponse toResponse(Dish domain);

}