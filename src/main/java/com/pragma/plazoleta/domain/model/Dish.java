package com.pragma.plazoleta.domain.model;


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

    private Integer price;

    private Restaurant restaurant;

    private String imageUrl;

    private Boolean active;

}
