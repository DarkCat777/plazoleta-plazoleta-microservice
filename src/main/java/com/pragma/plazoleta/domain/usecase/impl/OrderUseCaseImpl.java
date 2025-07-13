package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDetail;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.validation.Validation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;

@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {

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
}
