package com.pragma.plazoleta.restaurant.application.service;

import com.pragma.plazoleta.restaurant.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.domain.port.output.UserRoleValidatorPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseImplTest {
    @Mock
    private RestaurantRepositoryPort restaurantRepositoryPort;

    @Mock
    private UserRoleValidatorPort userRoleValidatorPort;

    @InjectMocks
    private CreateRestaurantUseCaseImpl createRestaurantUseCaseImpl;

    @Test
    void shouldCreateRestaurantWhenOwnerIsValid() {
        // given
        CreateRestaurantCommand command = new CreateRestaurantCommand(
                "Pizza Pragma", "Av. Siempre Viva 123", "+51987654321",
                "http://image.com/logo.png", "123456789", 1L
        );

        Restaurant expectedRestaurant = Restaurant.builder()
                .name(command.getName())
                .address(command.getAddress())
                .phone(command.getPhone())
                .logoUrl(command.getLogoUrl())
                .nit(command.getNit())
                .ownerId(command.getOwnerId())
                .build();

        when(userRoleValidatorPort.hasOwnerRole(command.getOwnerId())).thenReturn(true);
        when(restaurantRepositoryPort.save(any(Restaurant.class))).thenReturn(expectedRestaurant);

        // when
        Restaurant result = createRestaurantUseCaseImpl.createRestaurant(command);

        // then
        assertNotNull(result);
        assertEquals("Pizza Pragma", result.getName());
        verify(userRoleValidatorPort).hasOwnerRole(command.getOwnerId());
        verify(restaurantRepositoryPort).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // given
        CreateRestaurantCommand command = new CreateRestaurantCommand(
                "Pragma Sushi", "Calle Falsa 123", "999999999",
                "http://image.com/logo2.png", "987654321", 2L
        );

        when(userRoleValidatorPort.hasOwnerRole(command.getOwnerId())).thenReturn(false);

        // when & then
        InvalidOwnerException exception = assertThrows(
                InvalidOwnerException.class,
                () -> createRestaurantUseCaseImpl.createRestaurant(command)
        );

        assertEquals("El usuario no tiene el rol OWNER", exception.getMessage());
        verify(userRoleValidatorPort).hasOwnerRole(command.getOwnerId());
        verifyNoInteractions(restaurantRepositoryPort);
    }
}
