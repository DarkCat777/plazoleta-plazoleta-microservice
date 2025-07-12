package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantResponseMapper {

    RestaurantItemPageResponse toItemPageResponse(Restaurant domain);

    @Mapping(target = "id", ignore = true)
    Restaurant toDomain(CreateRestaurantCommand request);

    RestaurantResponse toResponse(Restaurant restaurant);

}
