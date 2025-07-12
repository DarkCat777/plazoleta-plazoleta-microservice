package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.CreateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.dto.response.ErrorResponse;
import com.pragma.plazoleta.application.service.DishService;
import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

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
    public ResponseEntity<DishResponse> createDish(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Validated @RequestBody CreateDishCommand command
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dishService.createDish(authenticatedUser.getId(), command));
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
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long dishId,
            @Validated @RequestBody UpdateDishCommand command
    ) {
        return ResponseEntity.ok(dishService.updateDish(authenticatedUser.getId(), dishId, command));
    }

    @Operation(
            summary = "Actualizar estado activo/inactivo de un plato",
            description = "Permite al propietario cambiar el estado del plato",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del plato actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario no autorizado o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{dishId}/status")
    public ResponseEntity<DishResponse> updateDishStatus(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long dishId,
            @Validated @RequestBody UpdateStatusDishCommand command
    ) {
        return ResponseEntity.ok(dishService.updateDishStatus(authenticatedUser.getId(), dishId, command));
    }

    @Operation(
            summary = "Obtener platos paginados por restaurante y categoría",
            description = "Permite a los clientes obtener un listado paginado de platos de un restaurante filtrado por categoría. Se puede aplicar paginación y ordenamiento.",
            security = @SecurityRequirement(name = "Bearer Auth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado paginado de platos obtenido exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos o usuario no autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante o categoría no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/restaurant/{restaurantId}/category/{categoryId}")
    public ResponseEntity<Page<DishResponse>> getPagedDishByRestaurantIdAndCategory(
            @Parameter(description = "ID del restaurante", required = true, example = "1")
            @PathVariable Long restaurantId,
            @Parameter(description = "ID de la categoría de platos", required = true, example = "2")
            @PathVariable Long categoryId,
            @Parameter(description = "Parámetros de paginación y ordenamiento: page, size, sort", hidden = true)
            @SortDefault(sort = "name")
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(dishService.getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable));
    }
}