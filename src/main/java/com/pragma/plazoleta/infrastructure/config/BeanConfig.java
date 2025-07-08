package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.application.service.CreateRestaurantService;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.domain.port.output.RestaurantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            RestaurantRepository restaurantRepository,
            OwnerValidatorPort ownerValidatorPort
    ) {
        return new CreateRestaurantService(restaurantRepository, ownerValidatorPort);
    }
}
