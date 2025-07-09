package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.application.service.CreateDishUseCaseImpl;
import com.pragma.plazoleta.application.service.CreateRestaurantUseCaseImpl;
import com.pragma.plazoleta.application.service.UpdateDishUseCaseImpl;
import com.pragma.plazoleta.domain.port.input.CreateDishUseCase;
import com.pragma.plazoleta.domain.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.domain.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.domain.port.output.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            RestaurantRepositoryPort restaurantRepository,
            OwnerValidatorPort ownerValidatorPort
    ) {
        return new CreateRestaurantUseCaseImpl(restaurantRepository, ownerValidatorPort);
    }

    @Bean
    public CreateDishUseCase createDishUseCase(
            DishRepositoryPort dishRepository,
            CategoryRepositoryPort categoryRepository,
            RestaurantRepositoryPort restaurantRepository,
            OwnerValidatorPort ownerValidatorPort,
            UserPort userPort
    ) {
        return new CreateDishUseCaseImpl(
                dishRepository,
                categoryRepository,
                restaurantRepository,
                ownerValidatorPort,
                userPort
        );
    }

    @Bean
    public UpdateDishUseCase updateDishUseCase(
            DishRepositoryPort dishRepository,
            OwnerValidatorPort ownerValidatorPort,
            UserPort userPort
    ) {
        return new UpdateDishUseCaseImpl(
                dishRepository,
                ownerValidatorPort,
                userPort
        );
    }
}
