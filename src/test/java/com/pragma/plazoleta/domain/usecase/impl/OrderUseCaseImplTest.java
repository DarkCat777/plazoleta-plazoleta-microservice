package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.exception.OrderNotFoundException;
import com.pragma.plazoleta.domain.model.*;
import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.validation.errors.impl.FieldError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.pragma.plazoleta.domain.exception.BusinessLogicException.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseImplTest {

    private UserClientPort userClientPort;
    private NotificationClientPort notificationClientPort;
    private OrderRepositoryPort orderRepositoryPort;
    private DishRepositoryPort dishRepositoryPort;
    private RestaurantRepositoryPort restaurantRepositoryPort;
    private OrderUseCaseImpl orderUseCase;

    @BeforeEach
    void setUp() {
        userClientPort = mock(UserClientPort.class);
        notificationClientPort = mock(NotificationClientPort.class);
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        dishRepositoryPort = mock(DishRepositoryPort.class);
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        orderUseCase = new OrderUseCaseImpl(userClientPort, notificationClientPort, orderRepositoryPort, dishRepositoryPort, restaurantRepositoryPort);
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

    @Test
    void sendNotificationOfOrderReady_success() {
        Order order = Order.builder()
                .id(1L)
                .customerId(50L)
                .securityPin("1234")
                .build();

        User customer = new User();
        customer.setId(50L);
        customer.setPhoneNumber("+51987654321");

        when(userClientPort.getUserById(50L)).thenReturn(java.util.Optional.of(customer));

        // Ejecutar método privado mediante reflexión o a través de markOrderAsReady
        orderUseCaseTestHelperInvokeSendNotification(order);

        // Capturar el argumento
        ArgumentCaptor<OrderReadyNotification> captor = ArgumentCaptor.forClass(OrderReadyNotification.class);
        verify(notificationClientPort).notifyOrderReady(captor.capture());

        OrderReadyNotification sentNotification = captor.getValue();
        assertEquals(1L, sentNotification.getOrderId());
        assertEquals("+51987654321", sentNotification.getCustomerPhone());
        assertEquals("1234", sentNotification.getSecurityPin());
    }

    @Test
    void sendNotificationOfOrderReady_shouldHandleUserNotFound() {
        Order order = Order.builder().id(1L).customerId(99L).build();

        when(userClientPort.getUserById(99L)).thenReturn(java.util.Optional.empty());

        orderUseCaseTestHelperInvokeSendNotification(order);

        verify(notificationClientPort, never()).notifyOrderReady(any());
    }

    @Test
    void sendNotificationOfOrderReady_shouldHandleNotificationFailure() {
        Order order = Order.builder()
                .id(1L)
                .customerId(50L)
                .securityPin("1234")
                .build();

        User customer = new User();
        customer.setId(50L);
        customer.setPhoneNumber("+51987654321");

        when(userClientPort.getUserById(50L)).thenReturn(java.util.Optional.of(customer));
        doThrow(new RuntimeException("Notification error"))
                .when(notificationClientPort).notifyOrderReady(any());

        orderUseCaseTestHelperInvokeSendNotification(order);

        verify(notificationClientPort).notifyOrderReady(any());
        // No debe lanzar excepción
    }

    // Método auxiliar para invocar el método privado usando reflexión
    private void orderUseCaseTestHelperInvokeSendNotification(Order order) {
        try {
            java.lang.reflect.Method method = OrderUseCaseImpl.class
                    .getDeclaredMethod("sendNotificationOfOrderReady", Order.class);
            method.setAccessible(true);
            method.invoke(orderUseCase, order);
        } catch (Exception e) {
            fail("Error invoking private method: " + e.getMessage());
        }
    }

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

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(employee));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.markOrderAsDelivered(orderId, employeeId, pin);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepositoryPort).save(order);
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenOrderNotFound() {
        Long orderId = 1L;
        Long employeeId = 100L;

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderUseCase.markOrderAsDelivered(orderId, employeeId, "1234"));
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenOrderNotReady() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.IN_PREPARATION) // no READY
                .build();

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(orderId, employeeId, "1234"));
        assertTrue(ex.getMessage().contains("Solo se marcar"));
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenInvalidPin() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .chefId(employeeId)
                .securityPin("1234") // real
                .build();

        User employee = new User();
        employee.setId(employeeId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(employee));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(orderId, employeeId, "9999")); // distinto
        assertTrue(ex.getMessage().contains("pin"));
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenEmployeeNotFound() {
        Long orderId = 1L;
        Long employeeId = 100L;
        String pin = "1234";

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .chefId(employeeId)
                .securityPin(pin)
                .build();

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> orderUseCase.markOrderAsDelivered(orderId, employeeId, pin));
    }

    @Test
    void markOrderAsDelivered_shouldThrowWhenEmployeeMismatch() {
        Long orderId = 1L;
        Long employeeId = 100L;      // quien intenta marcar
        Long otherChefId = 200L;     // quien preparó

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .chefId(otherChefId) // diferente
                .securityPin("1234")
                .build();

        User employee = new User();
        employee.setId(employeeId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(employeeId)).thenReturn(java.util.Optional.of(employee));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsDelivered(orderId, employeeId, "1234"));
        assertTrue(ex.getMessage().contains("entregado"));
    }

    @Test
    void markOrderAsCancelled_success() {
        Long orderId = 1L;
        Long customerId = 50L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .customerId(customerId)
                .build();

        User customer = new User();
        customer.setId(customerId);

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(customerId)).thenReturn(java.util.Optional.of(customer));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderUseCase.markOrderAsCancelled(orderId, customerId);

        assertEquals(OrderStatus.CANCELED, result.getStatus());
        verify(orderRepositoryPort).save(order);
        // No se debe notificar
        verify(notificationClientPort, never()).notifyOrderCantCancelled(any());
    }

    @Test
    void markOrderAsCancelled_notPending_sendsNotificationAndThrows() {
        Long orderId = 1L;
        Long customerId = 50L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.IN_PREPARATION) // <- clave
                .customerId(customerId)
                .build();

        User customer = new User();
        customer.setId(customerId);
        customer.setPhoneNumber("+51987654321");

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(customerId)).thenReturn(java.util.Optional.of(customer));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsCancelled(orderId, customerId));
        assertTrue(ex.getMessage().contains("Solo se marcan")); // ajusta al mensaje final

        // Capturamos la notificación
        ArgumentCaptor<OrderCantCanceledNotification> captor = ArgumentCaptor.forClass(OrderCantCanceledNotification.class);
        verify(notificationClientPort).notifyOrderCantCancelled(captor.capture());

        OrderCantCanceledNotification sent = captor.getValue();
        assertEquals(orderId, sent.getOrderId());
        assertEquals(OrderStatus.IN_PREPARATION, sent.getOrderStatus());
        assertEquals("+51987654321", sent.getCustomerPhone());
    }

    @Test
    void markOrderAsCancelled_notPending_notificationFailsStillThrows() {
        Long orderId = 1L;
        Long customerId = 50L;

        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .customerId(customerId)
                .build();

        User customer = new User();
        customer.setId(customerId);
        customer.setPhoneNumber("+51900000000");

        when(orderRepositoryPort.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(userClientPort.getUserById(customerId)).thenReturn(java.util.Optional.of(customer));
        doThrow(new RuntimeException("Twilio fail"))
                .when(notificationClientPort).notifyOrderCantCancelled(any());

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> orderUseCase.markOrderAsCancelled(orderId, customerId));
        assertTrue(ex.getMessage().contains("Solo se marcan")); // mensaje esperado

        // Se intentó notificar aunque falló
        verify(notificationClientPort).notifyOrderCantCancelled(any());
    }


}
