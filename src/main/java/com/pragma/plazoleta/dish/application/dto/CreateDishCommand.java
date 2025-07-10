package com.pragma.plazoleta.dish.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDishCommand {

    @NotBlank
    private String name;

    @Positive(message = "El precio debe ser mayor a 0")
    private int price;

    @NotBlank
    private String description;

    @NotBlank
    private String imageUrl;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long restaurantId;
}

