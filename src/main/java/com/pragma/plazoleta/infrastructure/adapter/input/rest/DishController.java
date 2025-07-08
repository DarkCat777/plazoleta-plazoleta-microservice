package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.port.input.CreateDishUseCase;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final CreateDishUseCase createDishUseCase;
    private final DishMapper dishMapper;

    @Operation(
            summary = "Crear plato",
            description = "Permite al propietario de un restaurante crear un plato asociado a su restaurante",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @SecurityRequirement(name = "Bearer Auth")
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
}