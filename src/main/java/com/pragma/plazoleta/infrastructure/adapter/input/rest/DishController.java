package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.application.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.DishResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.ErrorResponse;
import com.pragma.plazoleta.infrastructure.adapter.mapper.DishMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final CreateDishUseCase createDishUseCase;
    private final UpdateDishUseCase updateDishUseCase;
    private final DishMapper dishMapper;

    @Operation(
            summary = "Crear plato",
            description = "Permite al propietario de un restaurante crear un plato asociado a su restaurante",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario no es propietario del restaurante",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría o Restaurante no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<DishResponse> createDish(@Validated @RequestBody CreateDishCommand command) {
        Dish createdDish = createDishUseCase.createDish(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dishMapper.toResponse(createdDish));
    }

    @Operation(
            summary = "Actualizar precio y descripción del plato",
            description = "Permite al propietario del restaurante actualizar el precio y la descripción de un plato existente. "
                    + "Solo el propietario del restaurante puede realizar esta operación.",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = DishResponse.class))),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Usuario no autorizado o datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{dishId}")
    public ResponseEntity<DishResponse> updateDish(
            @PathVariable Long dishId,
            @Validated @RequestBody UpdateDishCommand command
    ) {
        Dish dish = updateDishUseCase.updateDish(dishId, command);
        return ResponseEntity.ok(dishMapper.toResponse(dish));
    }
}