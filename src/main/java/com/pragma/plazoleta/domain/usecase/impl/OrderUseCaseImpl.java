package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.exception.OrderNotFoundException;
import com.pragma.plazoleta.domain.model.*;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.validation.Validation;
import com.pragma.plazoleta.domain.validation.rules.extractor.LongExtractor;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;

@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {

    private final UserClientPort userClientPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final DishRepositoryPort dishRepositoryPort;
    private final RestaurantRepositoryPort restaurantRepositoryPort;

    /**
     * Validación completa de la creación de un pedido.
     * Requisitos:
     * 1. Todos los platos deben existir y pertenecer al restaurante indicado.
     * 2. El pedido debe contener los campos obligatorios: customerId, restaurantId, y la lista de platos.
     * 3. Cada plato debe tener dishId y cantidad > 0.
     * 4. El cliente no debe tener un pedido activo (pendiente, en preparación o listo).
     */
    public void validateCreateOrder(Order order) {
        validateBasicStructure(order);                    // Req 2 y 3
        validateNoActiveOrders(order);                    // Req 4
        validateRestaurantExists(order);                  // Req 2
        validateDishesExistAndBelongToRestaurant(order);  // Req 1
    }

    // Requisitos 2 y 3: Campos obligatorios y detalle por plato
    private void validateBasicStructure(Order order) {
        Validation.builder(order)
                .notNull("customerId", Order::getCustomerId)
                .positive("customerId", Order::getCustomerId)
                .notNull("restaurantId", Order::getRestaurantId)
                .positive("restaurantId", Order::getRestaurantId)
                .notEmpty("dishes", Order::getDishes)
                .each("dishes", Order::getDishes, item -> item
                        .notNull("dish", OrderDetail::getDish)
                        .nested("dish", OrderDetail::getDish, v -> v
                                .notNull("id", Dish::getId)
                                .positive("id", Dish::getId)
                                .build()
                        )
                        .positive("quantity", OrderDetail::getQuantity)
                        .build()
                ).build()
                .validate();
    }

    // Requisito 4: No debe tener pedido activo
    private void validateNoActiveOrders(Order order) {
        boolean hasActiveOrders = orderRepositoryPort.existsByCustomerIdAndStatusIn(
                order.getCustomerId(),
                List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY)
        );
        if (hasActiveOrders) {
            throw new BusinessLogicException(PENDING_ORDER);
        }
    }

    // Requisito 2: Restaurante debe existir
    private void validateRestaurantExists(Order order) {
        if (!restaurantRepositoryPort.existsById(order.getRestaurantId())) {
            throw new BusinessLogicException(RESTAURANT_NOT_EXIST);
        }
    }

    // Requisito 1: Todos los platos deben existir y pertenecer al restaurante
    private void validateDishesExistAndBelongToRestaurant(Order order) {
        List<Long> dishIds = order.getDishes().stream().map(dish -> dish.getDish().getId()).toList();
        List<Dish> dishes = dishRepositoryPort.findAllById(dishIds);
        if (dishes.size() != dishIds.size()) {
            throw new BusinessLogicException(DISH_NOT_EXIST);
        }
        boolean validRestaurant = dishes.stream()
                .allMatch(dish -> dish.getRestaurant().getId().equals(order.getRestaurantId()));
        if (!validRestaurant) {
            throw new BusinessLogicException(DISHES_ARE_NOT_SAME_RESTAURANT);
        }
    }

    @Override
    public Order createOrder(Long customerId, Order order) {
        order.setCustomerId(customerId);

        validateCreateOrder(order);

        List<OrderDetail> details = order.getDishes().stream()
                .map(d -> new OrderDetail(null, d.getDish(), d.getQuantity()))
                .toList();

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setDishes(details);

        return orderRepositoryPort.save(order);
    }

    @Override
    public PaginationResult<Order> findOrdersByStatusForEmployee(Long employeeId, String status, PaginationQuery paginationQuery) {
        List<String> validStatus = Arrays.stream(OrderStatus.values()).map(Enum::name).toList();

        Validation.builder(null)
                .notNull("status", t -> status)
                .notBlank("status", t -> status)
                .in("status", t -> status, validStatus)
                .build()
                .validate();

        User user = userClientPort.getUserById(employeeId)
                .orElseThrow(() -> new InvalidOwnerException("No se ha encontrado el usuario propietario"));

        if (user.getRestaurantId() == null) {
            throw new InvalidOwnerException("El usuario propietario no tiene un restaurante");
        }

        return orderRepositoryPort.findByRestaurantIdAndStatus(user.getRestaurantId(), status, paginationQuery);
    }

    @Override
    public Order assignOrderToEmployee(Long orderId, Long employeeId) {
        Validation.builder(null)
                .notNull("orderId", t -> orderId)
                .positive("orderId", (LongExtractor<Object>) t -> orderId)
                .notNull("employeeId", t -> employeeId)
                .positive("employeeId", (LongExtractor<Object>) t -> employeeId)
                .build()
                .validate();

        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessLogicException("Solo se pueden asignar pedidos pendientes");
        }

        User employee = userClientPort.getUserById(employeeId)
                .orElseThrow(() -> new UserNotFoundException(employeeId));

        if (!Objects.equals(order.getRestaurantId(), employee.getRestaurantId())) {
            throw new BusinessLogicException("No puedes asignarte pedidos de otro restaurante");
        }

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);

        return orderRepositoryPort.save(order);
    }
}
