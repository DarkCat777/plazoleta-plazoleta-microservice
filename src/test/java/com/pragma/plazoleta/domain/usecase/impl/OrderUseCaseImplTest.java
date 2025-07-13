package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.model.*;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseImplTest {

    private OrderRepositoryPort orderRepositoryPort;
    private DishRepositoryPort dishRepositoryPort;
    private RestaurantRepositoryPort restaurantRepositoryPort;
    private OrderUseCaseImpl orderUseCase;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        dishRepositoryPort = mock(DishRepositoryPort.class);
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        orderUseCase = new OrderUseCaseImpl(orderRepositoryPort, dishRepositoryPort, restaurantRepositoryPort);
    }

    @Test
    void createOrder_success() {
        Dish dish = Dish.builder().id(1L).restaurant(Restaurant.builder().id(1L).build()).build();
        OrderDetail detail = new OrderDetail(null, dish, 2);
        Order order = Order.builder()
                .restaurantId(1L)
                .dishes(List.of(detail))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(restaurantRepositoryPort.existsById(1L)).thenReturn(true);
        when(dishRepositoryPort.findAllById(List.of(1L))).thenReturn(List.of(dish));
        when(orderRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.createOrder(1L, order);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getCustomerId());
        assertNotNull(result.getCreatedAt());
        assertEquals(1, result.getDishes().size());
    }

    @Test
    void createOrder_shouldThrowWhenRestaurantNotExists() {
        Order order = Order.builder().restaurantId(99L)
                .dishes(List.of(new OrderDetail(null, Dish.builder().id(1L).build(), 1)))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(restaurantRepositoryPort.existsById(99L)).thenReturn(false);

        BusinessLogicException ex = assertThrows(BusinessLogicException.class, () -> orderUseCase.createOrder(1L, order));
        assertEquals(RESTAURANT_NOT_EXIST, ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowWhenDishDoesNotExist() {
        Order order = Order.builder().restaurantId(1L)
                .dishes(List.of(new OrderDetail(null, Dish.builder().id(999L).build(), 1)))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(restaurantRepositoryPort.existsById(1L)).thenReturn(true);
        when(dishRepositoryPort.findAllById(List.of(999L))).thenReturn(List.of());

        BusinessLogicException ex = assertThrows(BusinessLogicException.class, () -> orderUseCase.createOrder(1L, order));
        assertEquals(DISH_NOT_EXIST, ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowWhenDishFromDifferentRestaurant() {
        Dish dish = Dish.builder().id(1L).restaurant(Restaurant.builder().id(2L).build()).build();
        Order order = Order.builder().restaurantId(1L)
                .dishes(List.of(new OrderDetail(null, dish, 1)))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(restaurantRepositoryPort.existsById(1L)).thenReturn(true);
        when(dishRepositoryPort.findAllById(List.of(1L))).thenReturn(List.of(dish));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class, () -> orderUseCase.createOrder(1L, order));
        assertEquals(DISHES_ARE_NOT_SAME_RESTAURANT, ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowWhenCustomerHasActiveOrder() {
        Order order = Order.builder().restaurantId(1L)
                .dishes(List.of(new OrderDetail(null, Dish.builder().id(1L).restaurant(Restaurant.builder().id(1L).build()).build(), 1)))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(true);

        BusinessLogicException ex = assertThrows(BusinessLogicException.class, () -> orderUseCase.createOrder(1L, order));
        assertEquals(PENDING_ORDER, ex.getMessage());
    }
}
