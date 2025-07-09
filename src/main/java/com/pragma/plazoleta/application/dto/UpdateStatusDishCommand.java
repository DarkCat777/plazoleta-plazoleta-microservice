package com.pragma.plazoleta.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDishCommand {
    @NotNull(message = "El estado activo/inactivo no puede ser nulo")
    private Boolean active;
}
