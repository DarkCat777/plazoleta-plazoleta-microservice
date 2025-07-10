package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.dish.application.exception.DishNotFoundException;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateDishUseCaseImplTest {

    @Mock
    private DishRepositoryPort dishRepositoryPort;

    @Mock
    private OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

    @InjectMocks
    private UpdateDishUseCaseImpl updateDishUseCase;

    private final Long dishId = 1L;
    private final Long restaurantId = 100L;
    private final Long ownerId = 10L;

    private Dish dish;

    @BeforeEach
    void setUp() {
        dish = Dish.builder()
                .id(dishId)
                .restaurant(Restaurant.builder().id(restaurantId).build())
                .price(100)
                .description("Original description")
                .build();
    }

    @Test
    void shouldExecuteSuccessfully() {
        // Arrange
        UpdateDishCommand command = new UpdateDishCommand(150, "Nueva descripción");

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
        when(dishRepositoryPort.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Dish updatedDish = updateDishUseCase.execute(ownerId, dishId, command);

        // Assert
        assertEquals(150, updatedDish.getPrice());
        assertEquals("Nueva descripción", updatedDish.getDescription());
        verify(dishRepositoryPort).save(dish);
    }

    @Test
    void shouldThrowDishNotFoundException() {
        // Arrange
        UpdateDishCommand command = new UpdateDishCommand(150, "Nueva descripción");
        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DishNotFoundException.class, () ->
                updateDishUseCase.execute(ownerId, dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowInvalidOwnerException() {
        // Arrange
        UpdateDishCommand command = new UpdateDishCommand(150, "Nueva descripción");

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidOwnerException.class, () ->
                updateDishUseCase.execute(ownerId, dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }
}
