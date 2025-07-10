package com.pragma.plazoleta.restaurant.config;

import com.pragma.plazoleta.restaurant.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.restaurant.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.restaurant.application.service.CreateRestaurantUseCaseImpl;
import com.pragma.plazoleta.restaurant.application.service.GetPagedRestaurantUseCaseImpl;
import com.pragma.plazoleta.restaurant.domain.port.output.UserRoleValidatorPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantBeanConfig {

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            RestaurantRepositoryPort restaurantRepository,
            UserRoleValidatorPort userRoleValidatorPort
    ) {
        return new CreateRestaurantUseCaseImpl(restaurantRepository, userRoleValidatorPort);
    }

    @Bean
    public GetPagedRestaurantUseCase findPaginatedRestaurantUseCase(
            RestaurantRepositoryPort restaurantRepository
    ) {
        return new GetPagedRestaurantUseCaseImpl(restaurantRepository);
    }
}
