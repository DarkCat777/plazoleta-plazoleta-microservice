package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantUseCaseImplTest {

    private RestaurantRepositoryPort restaurantRepositoryPort;
    private OwnerValidatorPort ownerValidatorPort;
    private RestaurantUseCaseImpl restaurantUseCase;

    @BeforeEach
    void setUp() {
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        ownerValidatorPort = mock(OwnerValidatorPort.class);
        restaurantUseCase = new RestaurantUseCaseImpl(restaurantRepositoryPort, ownerValidatorPort);
    }

    @Test
    void createRestaurant_success() {
        Restaurant restaurant = new Restaurant(null, "La cocina", "Calle falsa", "+51999999999", "http://logo.png", "123456789", 1L);
        when(ownerValidatorPort.isOwner(1L)).thenReturn(true);
        when(restaurantRepositoryPort.save(restaurant)).thenReturn(restaurant);

        Restaurant saved = restaurantUseCase.createRestaurant(restaurant);

        assertNotNull(saved);
        verify(restaurantRepositoryPort).save(restaurant);
    }

    @Test
    void createRestaurant_shouldThrowValidationException() {
        Restaurant invalid = new Restaurant(null, "12345", "", "abc", "", "abc", -1L);

        assertThrows(ValidationException.class, () -> restaurantUseCase.createRestaurant(invalid));
        verify(restaurantRepositoryPort, never()).save(any());
    }

    @Test
    void createRestaurant_shouldThrowBusinessLogicException_whenUserIsNotOwner() {
        Restaurant restaurant = new Restaurant(null, "La cocina", "Calle falsa", "999999999", "http://logo.png", "123456789", 2L);
        when(ownerValidatorPort.isOwner(2L)).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> restaurantUseCase.createRestaurant(restaurant));
        verify(restaurantRepositoryPort, never()).save(any());
    }

    @Test
    void findAllPaginated_shouldDelegateToRepository() {
        PaginationQuery query = PaginationQuery.of(0, 10);
        PaginationResult<Restaurant> expected = new PaginationResult<>(List.of(), query, 0L);

        when(restaurantRepositoryPort.findAllPaged(query)).thenReturn(expected);

        PaginationResult<Restaurant> result = restaurantUseCase.findAllPaginated(query);

        assertSame(expected, result);
        verify(restaurantRepositoryPort).findAllPaged(query);
    }
}
