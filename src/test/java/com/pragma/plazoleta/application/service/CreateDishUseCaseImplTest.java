package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDishUseCaseImplTest {

    @Mock
    private DishRepositoryPort dishRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private RestaurantRepositoryPort restaurantRepositoryPort;

    @Mock
    private OwnerValidatorPort validator;

    @Mock
    private AuthenticationProviderPort authenticationProviderPort;

    @InjectMocks
    private CreateDishUseCaseImpl createDishUseCaseImpl;

    private CreateDishCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateDishCommand();
        command.setName("Ceviche");
        command.setPrice(20000);
        command.setDescription("Delicioso ceviche peruano");
        command.setImageUrl("http://image.com/ceviche.jpg");
        command.setCategoryId(1L);
        command.setRestaurantId(1L);
    }

    @Test
    void shouldCreateDishSuccessfully() {
        Long ownerId = 100L;

        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(validator.isOwnerOfRestaurant(ownerId, command.getRestaurantId())).thenReturn(true);
        when(categoryRepositoryPort.findById(command.getCategoryId()))
                .thenReturn(Optional.of(new Category(command.getCategoryId(), "Mariscos", "Comida del mar")));
        when(restaurantRepositoryPort.findById(command.getRestaurantId()))
                .thenReturn(Optional.of(new Restaurant(command.getRestaurantId(), "La Mar", "Av. Peru", "123456789", "NIT123", "http://logo.com", ownerId)));

        Dish savedDish = Dish.builder()
                .id(1L)
                .name(command.getName())
                .price(command.getPrice())
                .description(command.getDescription())
                .imageUrl(command.getImageUrl())
                .active(true)
                .build();

        when(dishRepositoryPort.save(any(Dish.class))).thenReturn(savedDish);

        Dish result = createDishUseCaseImpl.createDish(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ceviche");
        assertThat(result.isActive()).isTrue();
        verify(dishRepositoryPort).save(any(Dish.class));
    }

    @Test
    void shouldThrowExceptionIfUserIsNotOwnerOfRestaurant() {
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(99L);
        when(validator.isOwnerOfRestaurant(99L, command.getRestaurantId())).thenReturn(false);

        assertThatThrownBy(() -> createDishUseCaseImpl.createDish(command))
                .isInstanceOf(InvalidOwnerException.class)
                .hasMessageContaining("Solo el propietario del restaurante puede crear platos.");
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFound() {
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(1L);
        when(validator.isOwnerOfRestaurant(1L, command.getRestaurantId())).thenReturn(true);
        when(categoryRepositoryPort.findById(command.getCategoryId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createDishUseCaseImpl.createDish(command))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionIfRestaurantNotFound() {
        when(authenticationProviderPort.getAuthenticatedUserId()).thenReturn(1L);
        when(validator.isOwnerOfRestaurant(1L, command.getRestaurantId())).thenReturn(true);
        when(categoryRepositoryPort.findById(command.getCategoryId()))
                .thenReturn(Optional.of(new Category(1L, "Cat", "Desc")));
        when(restaurantRepositoryPort.findById(command.getRestaurantId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createDishUseCaseImpl.createDish(command))
                .isInstanceOf(RestaurantNotFoundException.class);
    }
}
