package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.UpdateStatusDishCommand;
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
class UpdateStatusDishUseCaseImplTest {

    @Mock
    private DishRepositoryPort dishRepositoryPort;

    @Mock
    private OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

    @InjectMocks
    private UpdateStatusDishUseCaseImpl updateStatusDishUseCase;

    private final Long dishId = 1L;
    private final Long restaurantId = 100L;
    private final Long ownerId = 10L;

    private Dish dish;

    @BeforeEach
    void setUp() {
        dish = Dish.builder()
                .id(dishId)
                .restaurant(Restaurant.builder().id(restaurantId).build())
                .active(false)
                .build();
    }

    @Test
    void shouldUpdateDishStatusSuccessfully() {
        // Arrange
        UpdateStatusDishCommand command = new UpdateStatusDishCommand(true);

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
        when(dishRepositoryPort.save(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Dish result = updateStatusDishUseCase.updateDishStatus(ownerId, dishId, command);

        // Assert
        assertTrue(result.isActive());
        verify(dishRepositoryPort).save(dish);
    }

    @Test
    void shouldThrowExceptionIfDishNotFound() {
        // Arrange
        UpdateStatusDishCommand command = new UpdateStatusDishCommand(true);
        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DishNotFoundException.class, () ->
                updateStatusDishUseCase.updateDishStatus(ownerId, dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfUserIsNotOwnerOfRestaurant() {
        // Arrange
        UpdateStatusDishCommand command = new UpdateStatusDishCommand(true);

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidOwnerException.class, () ->
                updateStatusDishUseCase.updateDishStatus(ownerId, dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }
}
