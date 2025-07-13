package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDishCommand {

    private String name;

    private Integer price;

    private String description;

    private String imageUrl;

    private Long categoryId;

    private Long restaurantId;

}

