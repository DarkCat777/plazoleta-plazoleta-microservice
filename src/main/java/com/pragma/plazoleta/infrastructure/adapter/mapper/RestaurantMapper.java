package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.RestaurantResponse;
import com.pragma.plazoleta.infrastructure.adapter.output.model.JpaRestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantMapper extends BaseMapper<Restaurant, JpaRestaurantEntity, RestaurantResponse> {
}
