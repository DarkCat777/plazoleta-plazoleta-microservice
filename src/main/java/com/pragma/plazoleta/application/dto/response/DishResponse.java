package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishResponse {

    private Long id;

    private String name;

    private Integer price;

    private String description;

    private String imageUrl;

    private Boolean active;

    private Long restaurantId;

    private Long categoryId;
}
