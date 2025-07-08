package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.DishResponse;
import com.pragma.plazoleta.infrastructure.adapter.output.model.JpaDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryMapper.class})
public interface DishMapper extends BaseMapper<Dish, JpaDishEntity, DishResponse> {
}