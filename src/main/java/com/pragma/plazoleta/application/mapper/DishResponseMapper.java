package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryResponseMapper.class})
public interface DishResponseMapper {

    @Mapping(source = "restaurant.id", target = "restaurantId")
    DishResponse toResponse(Dish domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant.id", source = "restaurantId")
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "active", ignore = true)
    Dish toDomain(CreateDishCommand command);

    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", ignore = true)
    Dish toDomain(UpdateDishCommand command);

    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "category", ignore = true)
    Dish toDomain(UpdateStatusDishCommand command);
}