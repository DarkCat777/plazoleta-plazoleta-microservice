package com.pragma.plazoleta.dish.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDishCommand {

    @Positive(message = "El precio debe ser mayor a 0")
    private int price;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;
}
