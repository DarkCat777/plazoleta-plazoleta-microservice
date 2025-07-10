package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.CreateDishCommand;
import com.pragma.plazoleta.dish.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.restaurant.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.dish.domain.model.Category;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.dish.domain.port.output.CategoryRepositoryPort;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
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
    private OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

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
    void shouldExecuteSuccessfully() {
        Long ownerId = 100L;

        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(ownerId, command.getRestaurantId())).thenReturn(true);
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

        Dish result = createDishUseCaseImpl.execute(ownerId, command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ceviche");
        assertThat(result.isActive()).isTrue();
        verify(dishRepositoryPort).save(any(Dish.class));
    }

    @Test
    void shouldThrowExceptionIfUserIsNotOwnerOfRestaurant() {
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(99L, command.getRestaurantId())).thenReturn(false);

        assertThatThrownBy(() -> createDishUseCaseImpl.execute(99L, command))
                .isInstanceOf(InvalidOwnerException.class)
                .hasMessageContaining("Solo el propietario del restaurante puede crear platos.");
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFound() {
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(1L, command.getRestaurantId())).thenReturn(true);
        when(categoryRepositoryPort.findById(command.getCategoryId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createDishUseCaseImpl.execute(1L, command))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionIfRestaurantNotFound() {
        when(ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(1L, command.getRestaurantId())).thenReturn(true);
        when(categoryRepositoryPort.findById(command.getCategoryId()))
                .thenReturn(Optional.of(new Category(1L, "Cat", "Desc")));
        when(restaurantRepositoryPort.findById(command.getRestaurantId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createDishUseCaseImpl.execute(1L, command))
                .isInstanceOf(RestaurantNotFoundException.class);
    }
}
