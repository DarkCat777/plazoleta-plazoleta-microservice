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
import com.pragma.plazoleta.domain.usecase.NotificationUseCase;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.usecase.TraceUseCase;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseImplTest {

    private UserClientPort userClientPort;
    private OrderRepositoryPort orderRepositoryPort;
    private DishRepositoryPort dishRepositoryPort;
    private RestaurantRepositoryPort restaurantRepositoryPort;
    private NotificationUseCase notificationUseCase;
    private TraceUseCase traceUseCase;

    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        userClientPort = mock(UserClientPort.class);
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        dishRepositoryPort = mock(DishRepositoryPort.class);
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        notificationUseCase = mock(NotificationUseCase.class);
        traceUseCase = mock(TraceUseCase.class);
        orderUseCase = new OrderUseCaseImpl(
                userClientPort,
                orderRepositoryPort,
                dishRepositoryPort,
                restaurantRepositoryPort,
                notificationUseCase,
                traceUseCase
        );
    }

    /* ------------------------------------------------------------------
     * createOrder
     * ------------------------------------------------------------------ */
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

        // Se registra trazabilidad: prev=null, new=PENDING
        verify(traceUseCase).traceChangeStatusOrder(result, null, null, OrderStatus.PENDING);
        // No notificación en creación
        verifyNoInteractions(notificationUseCase);
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void createOrder_shouldThrowWhenCustomerHasActiveOrder() {
        Order order = Order.builder().restaurantId(1L)
                .dishes(List.of(new OrderDetail(null,
                        Dish.builder().id(1L).restaurant(Restaurant.builder().id(1L).build()).build(), 1)))
                .build();

        when(orderRepositoryPort.existsByCustomerIdAndStatusIn(eq(1L), anyList())).thenReturn(true);

        BusinessLogicException ex = assertThrows(BusinessLogicException.class, () -> orderUseCase.createOrder(1L, order));
        assertEquals(PENDING_ORDER, ex.getMessage());
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }


    /* ------------------------------------------------------------------
     * findOrdersByStatusForEmployee
     * ------------------------------------------------------------------ */
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
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
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void findOrdersByStatusForEmployee_shouldThrowWhenInvalidStatus() {
        Long employeeId = 100L;
        String status = "INVALID_STATUS";
        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> orderUseCase.findOrdersByStatusForEmployee(employeeId, status, paginationQuery));

        assertTrue(((FieldError) exception.getErrors().get(0)).getField().contains("status"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }


    /* ------------------------------------------------------------------
     * assignOrderToEmployee
     * ------------------------------------------------------------------ */
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

        verify(traceUseCase).traceChangeStatusOrder(result, employeeId, OrderStatus.PENDING, OrderStatus.IN_PREPARATION);
        verifyNoInteractions(notificationUseCase);
    }

    @Test
    void assignOrderToEmployee_shouldThrowWhenOrderNotFound() {
        Long orderId = 1L;
        Long employeeId = 100L;

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
        verifyNoInteractions(notificationUseCase, traceUseCase);
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

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(BusinessLogicException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
        verifyNoInteractions(notificationUseCase, traceUseCase);
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

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
        verifyNoInteractions(notificationUseCase, traceUseCase);
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

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(Optional.of(employee));

        assertThrows(BusinessLogicException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }


    /* ------------------------------------------------------------------
     * markOrderAsReady
     * ------------------------------------------------------------------ */
    @Test
    void markOrderAsReady_success() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.IN_PREPARATION)
                .chefId(employeeId)
                .customerId(50L)
                .build();

        User employee = new User();
        employee.setId(employeeId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(Optional.of(employee));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.markOrderAsReady(orderId, employeeId);

        assertEquals(OrderStatus.READY, result.getStatus());
        assertNotNull(result.getSecurityPin());
        assertEquals(4, result.getSecurityPin().length());

        verify(orderRepositoryPort).save(order);
        verify(notificationUseCase).sendNotificationOfOrderReady(result);
        verify(traceUseCase).traceChangeStatusOrder(result, employeeId, OrderStatus.IN_PREPARATION, OrderStatus.READY);
    }

    @Test
    void markOrderAsReady_shouldThrowWhenOrderNotFound() {
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderUseCase.markOrderAsReady(1L, 10L));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsReady_shouldThrowWhenOrderNotInPreparation() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsReady(1L, 10L));
        assertTrue(ex.getMessage().contains("Solo se marcar como listos"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsReady_shouldThrowWhenEmployeeNotFound() {
        Order order = Order.builder().id(1L).status(OrderStatus.IN_PREPARATION).chefId(10L).build();
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(20L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderUseCase.markOrderAsReady(1L, 20L));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsReady_shouldThrowWhenEmployeeMismatch() {
        Order order = Order.builder().id(1L).status(OrderStatus.IN_PREPARATION).chefId(10L).build();
        User otherEmp = new User();
        otherEmp.setId(20L);
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(20L)).thenReturn(Optional.of(otherEmp));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsReady(1L, 20L));
        assertTrue(ex.getMessage().contains("No puedes marcar como listo"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }


    /* ------------------------------------------------------------------
     * markOrderAsDelivered
     * ------------------------------------------------------------------ */
    @Test
    void markOrderAsDelivered_success() {
        Long orderId = 1L;
        Long employeeId = 100L;
        String pin = "1234";

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .chefId(employeeId)
                .securityPin(pin)
                .build();

        User employee = new User();
        employee.setId(employeeId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(Optional.of(employee));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.markOrderAsDelivered(orderId, employeeId, pin);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepositoryPort).save(order);
        verify(traceUseCase).traceChangeStatusOrder(result, employeeId, OrderStatus.READY, OrderStatus.DELIVERED);
        verifyNoInteractions(notificationUseCase);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenOrderNotFound() {
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class,
                () -> orderUseCase.markOrderAsDelivered(1L, 10L, "1234"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenOrderNotReady() {
        Order order = Order.builder().id(1L).status(OrderStatus.IN_PREPARATION).build();
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(1L, 10L, "1234"));
        assertTrue(ex.getMessage().contains("Solo se marcar como listos"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenInvalidPin() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .chefId(10L)
                .securityPin("1234")
                .build();
        User emp = new User();
        emp.setId(10L);

        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(10L)).thenReturn(Optional.of(emp));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(1L, 10L, "9999"));
        assertTrue(ex.getMessage().contains("pin"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenEmployeeNotFound() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .chefId(10L)
                .securityPin("1234")
                .build();
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(20L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> orderUseCase.markOrderAsDelivered(1L, 20L, "1234"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenEmployeeMismatch() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .chefId(10L)
                .securityPin("1234")
                .build();
        User emp = new User();
        emp.setId(20L);

        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(20L)).thenReturn(Optional.of(emp));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(1L, 20L, "1234"));
        assertTrue(ex.getMessage().contains("entregado"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }


    /* ------------------------------------------------------------------
     * markOrderAsCancelled
     * ------------------------------------------------------------------ */
    @Test
    void markOrderAsCanceled_success() {
        Long orderId = 1L;
        Long customerId = 50L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .customerId(customerId)
                .build();

        User customer = new User();
        customer.setId(customerId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.markOrderAsCanceled(orderId, customerId);

        assertEquals(OrderStatus.CANCELED, result.getStatus());
        verify(orderRepositoryPort).save(order);
        verify(traceUseCase).traceChangeStatusOrder(result, null, OrderStatus.PENDING, OrderStatus.CANCELED);
        verifyNoInteractions(notificationUseCase);
    }

    @Test
    void markOrderAsCanceled_notPending_sendsNotificationAndThrows() {
        Long orderId = 1L;
        Long customerId = 50L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.IN_PREPARATION) // <- clave
                .customerId(customerId)
                .build();

        User customer = new User();
        customer.setId(customerId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(customerId)).thenReturn(Optional.of(customer));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsCanceled(orderId, customerId));
        assertTrue(ex.getMessage().contains("Solo se marcan"));

        // Notificación de que no puede cancelarse
        verify(notificationUseCase).sendNotificationOfOrderInPreparation(order);
        verifyNoInteractions(traceUseCase);
    }

    @Test
    void markOrderAsCancelled_shouldThrowWhenOrderNotFound() {
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderUseCase.markOrderAsCanceled(1L, 2L));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsCanceled_shouldThrowWhenCustomerNotFound() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).customerId(100L).build();
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderUseCase.markOrderAsCanceled(1L, 2L));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }

    @Test
    void markOrderAsCancelled_shouldThrowWhenOrderBelongsToAnotherCustomer() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).customerId(100L).build();
        User customer = new User();
        customer.setId(2L);

        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(userClientPort.getUserById(2L)).thenReturn(Optional.of(customer));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsCanceled(1L, 2L));
        assertTrue(ex.getMessage().contains("No puedes cancelar"));
        verifyNoInteractions(notificationUseCase, traceUseCase);
    }
}
