package com.pragma.plazoleta.domain.model;


import lombok.*;

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
