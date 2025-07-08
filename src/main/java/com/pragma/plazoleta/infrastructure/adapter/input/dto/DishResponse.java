package com.pragma.plazoleta.infrastructure.adapter.input.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishResponse {

    private Long id;
    private String name;
    private int price;
    private String description;
    private String imageUrl;
    private boolean active;
    private Long restaurantId;
    private CategoryResponse category;
}
