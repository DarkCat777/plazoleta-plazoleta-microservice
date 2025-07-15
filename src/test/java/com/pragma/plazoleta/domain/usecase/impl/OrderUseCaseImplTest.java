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
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseImplTest {

    private UserClientPort userClientPort;
    private OrderRepositoryPort orderRepositoryPort;
    private DishRepositoryPort dishRepositoryPort;
    private RestaurantRepositoryPort restaurantRepositoryPort;
    private OrderUseCaseImpl orderUseCase;

    @BeforeEach
    void setUp() {
        userClientPort = mock(UserClientPort.class);
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        dishRepositoryPort = mock(DishRepositoryPort.class);
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        orderUseCase = new OrderUseCaseImpl(userClientPort, orderRepositoryPort, dishRepositoryPort, restaurantRepositoryPort);
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

    @Test
    void findOrdersByStatusForEmployee_success() {
        Long employeeId = 100L;
        String status = "PENDING";
        Long restaurantId = 10L;

        User user = new User();
        user.setId(employeeId);
        user.setRestaurantId(restaurantId);

        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);
        PaginationResult<Order> expectedResult = new PaginationResult<>(List.of(), paginationQuery, 0L);

        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(user));
        when(orderRepositoryPort.findByRestaurantIdAndStatus(restaurantId, status, paginationQuery)).thenReturn(expectedResult);

        PaginationResult<Order> result = orderUseCase.findOrdersByStatusForEmployee(employeeId, status, paginationQuery);

        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userClientPort).getUserById(employeeId);
        verify(orderRepositoryPort).findByRestaurantIdAndStatus(restaurantId, status, paginationQuery);
    }

    @Test
    void findOrdersByStatusForEmployee_shouldThrowWhenUserNotFound() {
        Long employeeId = 100L;
        String status = "PENDING";
        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);

        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(InvalidOwnerException.class,
                () -> orderUseCase.findOrdersByStatusForEmployee(employeeId, status, paginationQuery));

        assertEquals("No se ha encontrado el usuario propietario", exception.getMessage());
    }

    @Test
    void findOrdersByStatusForEmployee_shouldThrowWhenUserHasNoRestaurant() {
        Long employeeId = 100L;
        String status = "PENDING";
        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);

        User user = new User();
        user.setId(employeeId);
        user.setRestaurantId(null);

        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(user));

        Exception exception = assertThrows(InvalidOwnerException.class,
                () -> orderUseCase.findOrdersByStatusForEmployee(employeeId, status, paginationQuery));

        assertEquals("El usuario propietario no tiene un restaurante", exception.getMessage());
    }

    @Test
    void findOrdersByStatusForEmployee_shouldThrowWhenInvalidStatus() {
        Long employeeId = 100L;
        String status = "INVALID_STATUS";
        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> orderUseCase.findOrdersByStatusForEmployee(employeeId, status, paginationQuery));

        assertTrue(((FieldError) exception.getErrors().get(0)).getField().contains("status"));
    }

    @Test
    void assignOrderToEmployee_success() {
        Long orderId = 1L;
        Long employeeId = 100L;
        Long restaurantId = 10L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .restaurantId(restaurantId)
                .build();

        User employee = new User();
        employee.setId(employeeId);
        employee.setRestaurantId(restaurantId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(employee));
        when(orderRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.assignOrderToEmployee(orderId, employeeId);

        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());
        assertEquals(employeeId, result.getChefId());
        verify(orderRepositoryPort).save(order);
    }

    @Test
    void assignOrderToEmployee_shouldThrowWhenOrderNotFound() {
        Long orderId = 1L;
        Long employeeId = 100L;

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
    }

    @Test
    void assignOrderToEmployee_shouldThrowWhenOrderIsNotPending() {
        Long orderId = 1L;
        Long employeeId = 100L;
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.CANCELED)
                .restaurantId(10L)
                .build();

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));

        assertThrows(BusinessLogicException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
    }

    @Test
    void assignOrderToEmployee_shouldThrowWhenEmployeeNotFound() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .restaurantId(10L)
                .build();

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
    }

    @Test
    void assignOrderToEmployee_shouldThrowWhenEmployeeFromOtherRestaurant() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .restaurantId(10L)
                .build();

        User employee = new User();
        employee.setId(employeeId);
        employee.setRestaurantId(99L); // otro restaurante

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(employee));

        assertThrows(BusinessLogicException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
    }
}
