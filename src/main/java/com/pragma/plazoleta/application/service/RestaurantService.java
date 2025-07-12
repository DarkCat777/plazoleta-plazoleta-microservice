package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantService {

    RestaurantResponse createRestaurant(CreateRestaurantCommand request);

    Page<RestaurantItemPageResponse> findAllPaginated(Pageable pageable);

}
