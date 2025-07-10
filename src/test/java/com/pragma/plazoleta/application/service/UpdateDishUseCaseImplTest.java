package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.UpdateDishCommand;
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
class UpdateDishUseCaseImplTest {

    @Mock
    private DishRepositoryPort dishRepositoryPort;

    @Mock
    private OwnerValidatorPort ownerValidatorPort;

    @Mock
    private AuthenticationProviderPort authenticationProviderPort;

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
    void shouldUpdateDishSuccessfully() {
        // Arrange
        UpdateDishCommand command = new UpdateDishCommand(150, "Nueva descripción");

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
        when(dishRepositoryPort.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Dish updatedDish = updateDishUseCase.updateDish(dishId, command);

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
                updateDishUseCase.updateDish(dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowInvalidOwnerException() {
        // Arrange
        UpdateDishCommand command = new UpdateDishCommand(150, "Nueva descripción");

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(dish));
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidOwnerException.class, () ->
                updateDishUseCase.updateDish(dishId, command));
        verify(dishRepositoryPort, never()).save(any());
    }
}
