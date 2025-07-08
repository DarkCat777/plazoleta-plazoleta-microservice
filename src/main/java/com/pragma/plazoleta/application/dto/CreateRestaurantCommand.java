package com.pragma.plazoleta.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantCommand {

    @NotBlank
    @Pattern(regexp = "^(?!\\d+$).+", message = "El nombre no puede contener solo números")
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = "Teléfono inválido (solo números y opcional +)")
    private String phone;

    @NotBlank
    private String logoUrl;

    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "NIT debe contener solo números")
    private String nit;

    @NotNull
    private Long ownerId;
}
