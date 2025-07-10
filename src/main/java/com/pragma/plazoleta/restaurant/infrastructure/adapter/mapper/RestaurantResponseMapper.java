package com.pragma.plazoleta.restaurant.infrastructure.adapter.mapper;

import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.response.RestaurantResponse;
import com.pragma.plazoleta.shared.mapper.BaseResponseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantResponseMapper extends BaseResponseMapper<Restaurant, RestaurantResponse> {
    RestaurantItemPageResponse toItemPageResponse(Restaurant domain);
}
