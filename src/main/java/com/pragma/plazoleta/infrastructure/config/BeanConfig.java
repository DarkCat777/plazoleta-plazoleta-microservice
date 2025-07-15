package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.spi.persistence.CategoryRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.domain.usecase.impl.DishUseCaseImpl;
import com.pragma.plazoleta.domain.usecase.impl.OrderUseCaseImpl;
import com.pragma.plazoleta.domain.usecase.impl.RestaurantUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public DishUseCase createDishUseCase(
            DishRepositoryPort dishRepository,
            CategoryRepositoryPort categoryRepository,
            RestaurantRepositoryPort restaurantRepository,
            OwnerValidatorPort ownerValidator
    ) {
        return new DishUseCaseImpl(
                dishRepository,
                categoryRepository,
                restaurantRepository,
                ownerValidator
        );
    }

    @Bean
    public RestaurantUseCase createRestaurantUseCase(
            RestaurantRepositoryPort restaurantRepository,
            OwnerValidatorPort ownerValidator
    ) {
        return new RestaurantUseCaseImpl(
                restaurantRepository,
                ownerValidator
        );
    }


    @Bean
    public OrderUseCase createOrderUseCase(
            UserClientPort userClientPort,
            OrderRepositoryPort orderRepositoryPort,
            DishRepositoryPort dishRepositoryPort,
            RestaurantRepositoryPort restaurantRepositoryPort
    ) {
        return new OrderUseCaseImpl(
                userClientPort,
                orderRepositoryPort,
                dishRepositoryPort,
                restaurantRepositoryPort
        );
    }

}
