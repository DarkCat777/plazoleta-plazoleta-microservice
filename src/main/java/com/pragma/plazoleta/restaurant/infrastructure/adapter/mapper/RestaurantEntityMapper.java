package com.pragma.plazoleta.restaurant.infrastructure.adapter.mapper;

import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.output.model.JpaRestaurantEntity;
import com.pragma.plazoleta.shared.mapper.BaseEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantEntityMapper extends BaseEntityMapper<Restaurant, JpaRestaurantEntity> {
}
