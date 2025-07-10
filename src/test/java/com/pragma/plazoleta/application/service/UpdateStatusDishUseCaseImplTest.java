package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.AuthenticationProviderPort;
import com.pragma.plazoleta.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
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
    private OwnerValidatorPort ownerValidatorPort;

    @Mock
    private AuthenticationProviderPort authenticationProviderPort;

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
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
        when(dishRepositoryPort.save(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Dish result = updateStatusDishUseCase.updateDishStatus(dishId, command);

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
                updateStatusDishUseCase.updateDishStatus(dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfUserIsNotOwnerOfRestaurant() {
        // Arrange
        UpdateStatusDishCommand command = new UpdateStatusDishCommand(true);

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidOwnerException.class, () ->
                updateStatusDishUseCase.updateDishStatus(dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }
}
