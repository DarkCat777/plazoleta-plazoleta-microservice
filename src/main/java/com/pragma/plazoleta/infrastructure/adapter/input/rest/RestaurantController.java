package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.ErrorResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.RestaurantResponse;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantResponseMapper;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final RestaurantResponseMapper restaurantMapper;

    @Operation(
            summary = "Crear restaurante",
            description = "Permite crear un restaurante si el usuario es OWNER",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El ID del propietario no es válido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Validated @RequestBody CreateRestaurantCommand request) {
        Restaurant restaurant = createRestaurantUseCase.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantMapper.toResponse(restaurant));
    }

}