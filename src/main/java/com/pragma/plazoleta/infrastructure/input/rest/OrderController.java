package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.common.ErrorResponse;
import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.service.OrderService;
import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Crear una nueva orden",
            description = "Permite al usuario con rol CUSTOMER crear una orden en un restaurante específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (validaciones)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado (token faltante o inválido)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo rol CUSTOMER puede crear ordenes"),
            @ApiResponse(responseCode = "404", description = "Restaurante o plato no encontrado"),
            @ApiResponse(responseCode = "409", description = "El cliente ya tiene una orden pendiente")
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Validated @RequestBody CreateOrderCommand command
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(authenticatedUser.getId(), command));
    }
}
