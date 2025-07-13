package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.*;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.persistence.CategoryRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseImplTest {

    private DishRepositoryPort dishRepositoryPort;
    private CategoryRepositoryPort categoryRepositoryPort;
    private RestaurantRepositoryPort restaurantRepositoryPort;
    private OwnerValidatorPort ownerValidatorPort;
    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        dishRepositoryPort = mock(DishRepositoryPort.class);
        categoryRepositoryPort = mock(CategoryRepositoryPort.class);
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        ownerValidatorPort = mock(OwnerValidatorPort.class);
        dishUseCase = new DishUseCaseImpl(dishRepositoryPort, categoryRepositoryPort, restaurantRepositoryPort, ownerValidatorPort);
    }

    @Test
    void createDish_success() {
        Long ownerId = 1L;
        Category category = new Category(1L, "Postres", "Dulces deliciosos");
        Restaurant restaurant = new Restaurant(1L, "La cocina", "Calle falsa", "9999", "http://logo.png", "123456789", ownerId);
        Dish dish = new Dish(null, "Tarta", category, "Dulce", 12, restaurant, "http://img.png", true);

        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(category));
        when(restaurantRepositoryPort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, 1L)).thenReturn(true);
        when(dishRepositoryPort.save(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dish result = dishUseCase.createDish(ownerId, dish);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(dishRepositoryPort).save(dish);
    }

    @Test
    void createDish_invalidOwner_shouldThrow() {
        Long ownerId = 2L;
        Category category = new Category(1L, "Postres", "Dulces deliciosos");
        Restaurant restaurant = new Restaurant(1L, "La cocina", "Calle falsa", "9999", "http://logo.png", "123456789", 1L);
        Dish dish = new Dish(null, "Tarta", category, "Dulce", 12, restaurant, "http://img.png", true);

        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(category));
        when(restaurantRepositoryPort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, 1L)).thenReturn(false);

        assertThrows(InvalidOwnerException.class, () -> dishUseCase.createDish(ownerId, dish));
    }

    @Test
    void updateDish_success() {
        Long ownerId = 1L;
        Long dishId = 10L;
        Category category = new Category(1L, "Cat", "Desc");
        Restaurant restaurant = new Restaurant(1L, "Res", "Dir", "999", "img", "NIT123", ownerId);
        Dish existing = new Dish(dishId, "Tarta", category, "Dulce", 10, restaurant, "url", true);

        Dish command = new Dish(null, null, null, "Nueva descripcion", 15, null, null, null);

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(existing));
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, 1L)).thenReturn(true);
        when(dishRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Dish result = dishUseCase.updateDish(ownerId, dishId, command);

        assertEquals("Nueva descripcion", result.getDescription());
        assertEquals(15, result.getPrice());
    }

    @Test
    void updateDishStatus_success() {
        Long ownerId = 1L;
        Long dishId = 10L;
        Category category = new Category(1L, "Cat", "Desc");
        Restaurant restaurant = new Restaurant(1L, "Res", "Dir", "999", "img", "NIT123", ownerId);
        Dish existing = new Dish(dishId, "Tarta", category, "Dulce", 10, restaurant, "url", true);

        Dish command = new Dish();
        command.setActive(false);

        when(dishRepositoryPort.findById(dishId)).thenReturn(Optional.of(existing));
        when(ownerValidatorPort.isOwnerOfRestaurant(ownerId, 1L)).thenReturn(true);
        when(dishRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Dish result = dishUseCase.updateDishStatus(ownerId, dishId, command);

        assertFalse(result.getActive());
    }

    @Test
    void getPagedDishByRestaurantIdAndCategoryId_shouldDelegate() {
        Long restaurantId = 1L;
        Long categoryId = 2L;
        PaginationQuery query = PaginationQuery.of(0, 10);
        PaginationResult<Dish> page = new PaginationResult<>(List.of(), query, 10L);

        when(dishRepositoryPort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, query)).thenReturn(page);

        PaginationResult<Dish> result = dishUseCase.getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, query);

        assertSame(page, result);
    }
}
