package com.pragma.plazoleta.dish.domain.model;


import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private Long id;
    private String name;
    private Category category;
    private String description;
    private int price;
    private Restaurant restaurant;
    private String imageUrl;
    private boolean active;
}
