package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.ErrorResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.RestaurantResponse;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetPagedRestaurantUseCase getPagedRestaurantUseCase;
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

    @Operation(
            summary = "Obtener restaurantes paginados",
            description = "Permite a un usuario con rol CUSTOMER obtener una lista paginada de restaurantes registrados en el sistema.",
            security = @SecurityRequirement(name = "Bearer Auth"),
            parameters = {
                    @Parameter(name = "page", description = "Número de página a recuperar (empezando desde 0)", example = "0"),
                    @Parameter(name = "size", description = "Cantidad de elementos por página", example = "10")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lista de restaurantes obtenida exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene el rol requerido",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<PaginatedResult<RestaurantItemPageResponse>> getPaginatedRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginatedResult<Restaurant> restaurantPage = getPagedRestaurantUseCase.findAllPaginated(PaginationQuery.of(page, size));
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantPage.map(restaurantMapper::toItemPageResponse));
    }

}