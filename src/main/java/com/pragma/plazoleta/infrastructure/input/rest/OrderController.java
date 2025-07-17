package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.common.ErrorResponse;
import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.request.OrderByStatusQuery;
import com.pragma.plazoleta.application.dto.request.OrderSecurityPinQuery;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.service.OrderService;
import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody CreateOrderCommand command
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(authenticatedUser.getId(), command));
    }

    @Operation(
            summary = "Obtener pedidos por estado (solo para empleados)",
            description = "Devuelve una lista paginada de pedidos filtrados por estado, solo del restaurante al que pertenece el empleado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de pedidos obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "Filtro por estado del pedido", required = true)
            @RequestBody OrderByStatusQuery query,
            @Parameter(description = "Parámetros de paginación y ordenamiento: page, size, sort")
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByStatusForEmployee(authenticatedUser.getId(), query, pageable)
        );
    }

    @Operation(
            summary = "Asignar pedido a empleado",
            description = "Permite que un empleado se asigne a un pedido pendiente de su restaurante y cambie su estado a EN_PREPARATION."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido asignado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Validación fallida o negocio inválido (pedido ya asignado, empleado no pertenece al restaurante)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido o empleado no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado para acceder a este recurso",
                    content = @Content)
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{orderId}/assign")
    public ResponseEntity<OrderResponse> assignOrder(
            @Parameter(description = "ID del pedido que se va a asignar", required = true, example = "123")
            @PathVariable Long orderId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(orderService.assignOrderToEmployee(orderId, authenticatedUser.getId()));
    }


    @Operation(
            summary = "Marcar pedido como listo",
            description = "Permite que un empleado marque un pedido en estado `IN_PREPARATION` como `READY`. Genera un PIN de seguridad y notifica al cliente vía SMS.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido marcado como listo",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Reglas de negocio incumplidas",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{orderId}/mark-as-ready")
    public ResponseEntity<OrderResponse> markOrderAsReady(
            @Parameter(description = "ID del pedido que se va a marcar como listo", required = true, example = "123")
            @PathVariable Long orderId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(orderService.markOrderAsReady(orderId, authenticatedUser.getId()));
    }

    @Operation(
            summary = "Marcar pedido como entregado",
            description = """
                    Marca un pedido previamente *READY* como *DELIVERED* luego de validar:
                    - Que el pedido exista.
                    - Que el pedido esté en estado READY.
                    - Que el PIN de seguridad enviado coincida con el generado al marcar READY.
                    - Que el empleado autenticado sea el mismo que atendió/preparó el pedido (chef asignado).
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido marcado como entregado.",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Error de validación o reglas de negocio (estado inválido, PIN incorrecto, empleado no autorizado para este pedido).",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Pedido o empleado no encontrado.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado (el token no tiene rol EMPLOYEE)."),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{orderId}/mark-as-delivered")
    public ResponseEntity<OrderResponse> markOrderAsDelivered(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "ID del pedido que se va a marcar como entregado", required = true, example = "123")
            @PathVariable Long orderId,
            @RequestBody OrderSecurityPinQuery query
    ) {
        return ResponseEntity.ok(orderService.markOrderAsDelivered(orderId, authenticatedUser.getId(), query));
    }

}
