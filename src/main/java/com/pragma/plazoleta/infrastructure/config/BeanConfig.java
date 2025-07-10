package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.application.port.input.*;
import com.pragma.plazoleta.application.service.*;
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
    public GetPagedRestaurantUseCase findPaginatedRestaurantUseCase(
            RestaurantRepositoryPort restaurantRepository
    ) {
        return new GetPagedRestaurantUseCaseImpl(restaurantRepository);
    }

    @Bean
    public CreateDishUseCase createDishUseCase(
            DishRepositoryPort dishRepository,
            CategoryRepositoryPort categoryRepository,
            RestaurantRepositoryPort restaurantRepository,
            OwnerValidatorPort ownerValidatorPort,
            AuthenticationProviderPort authenticationProviderPort
    ) {
        return new CreateDishUseCaseImpl(
                dishRepository,
                categoryRepository,
                restaurantRepository,
                ownerValidatorPort,
                authenticationProviderPort
        );
    }

    @Bean
    public UpdateDishUseCase updateDishUseCase(
            DishRepositoryPort dishRepository,
            OwnerValidatorPort ownerValidatorPort,
            AuthenticationProviderPort authenticationProviderPort
    ) {
        return new UpdateDishUseCaseImpl(dishRepository, ownerValidatorPort, authenticationProviderPort);
    }

    @Bean
    public UpdateStatusDishUseCase updateActiveOrInactiveDishUseCase(
            DishRepositoryPort dishRepository,
            OwnerValidatorPort ownerValidatorPort,
            AuthenticationProviderPort authenticationProviderPort
    ) {
        return new UpdateStatusDishUseCaseImpl(dishRepository, ownerValidatorPort, authenticationProviderPort);
    }

    @Bean
    public GetPagedDishByRestaurantAndCategoryUseCase getPagedDishByRestaurantAndCategoryUseCase(DishRepositoryPort dishRepository) {
        return new GetPagedDishByRestaurantAndCategoryUseCaseImpl(dishRepository);
    }
}
