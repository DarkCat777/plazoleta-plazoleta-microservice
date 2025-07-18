package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.TraceClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.spi.persistence.CategoryRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.*;
import com.pragma.plazoleta.domain.usecase.impl.*;
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
    public NotificationUseCase createNotificationUseCase(
            UserClientPort userClientPort,
            NotificationClientPort notificationClientPort
    ) {
        return new NotificationUseCaseImpl(
                userClientPort,
                notificationClientPort
        );
    }

    @Bean
    public TraceUseCase createTraceUseCase(
            UserClientPort userClientPort,
            TraceClientPort traceClientPort
    ) {
        return new TraceUseCaseImpl(
                userClientPort,
                traceClientPort
        );
    }

    @Bean
    public OrderUseCase createOrderUseCase(
            UserClientPort userClientPort,
            OrderRepositoryPort orderRepositoryPort,
            DishRepositoryPort dishRepositoryPort,
            RestaurantRepositoryPort restaurantRepositoryPort,
            NotificationUseCase notificationUseCase,
            TraceUseCase traceUseCase
    ) {
        return new OrderUseCaseImpl(
                userClientPort,
                orderRepositoryPort,
                dishRepositoryPort,
                restaurantRepositoryPort,
                notificationUseCase,
                traceUseCase
        );
    }

}
