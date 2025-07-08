package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.domain.port.output.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OwnerValidatorPort ownerValidatorPort;

    @InjectMocks
    private CreateRestaurantService createRestaurantService;

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

        when(ownerValidatorPort.isOwner(command.getOwnerId())).thenReturn(true);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(expectedRestaurant);

        // when
        Restaurant result = createRestaurantService.createRestaurant(command);

        // then
        assertNotNull(result);
        assertEquals("Pizza Pragma", result.getName());
        verify(ownerValidatorPort).isOwner(command.getOwnerId());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // given
        CreateRestaurantCommand command = new CreateRestaurantCommand(
                "Pragma Sushi", "Calle Falsa 123", "999999999",
                "http://image.com/logo2.png", "987654321", 2L
        );

        when(ownerValidatorPort.isOwner(command.getOwnerId())).thenReturn(false);

        // when & then
        InvalidOwnerException exception = assertThrows(
                InvalidOwnerException.class,
                () -> createRestaurantService.createRestaurant(command)
        );

        assertEquals("El usuario no tiene el rol OWNER", exception.getMessage());
        verify(ownerValidatorPort).isOwner(command.getOwnerId());
        verifyNoInteractions(restaurantRepository);
    }
}
