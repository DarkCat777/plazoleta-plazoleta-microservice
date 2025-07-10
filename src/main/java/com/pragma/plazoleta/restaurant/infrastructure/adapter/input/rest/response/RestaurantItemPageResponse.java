package com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantItemPageResponse {
    private Long id;
    private String name;
    private String logoUrl;
}
