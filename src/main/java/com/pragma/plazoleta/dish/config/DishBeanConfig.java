package com.pragma.plazoleta.dish.config;

import com.pragma.plazoleta.dish.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.dish.application.port.input.GetPagedDishByRestaurantAndCategoryUseCase;
import com.pragma.plazoleta.dish.application.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.dish.application.port.input.UpdateStatusDishUseCase;
import com.pragma.plazoleta.dish.application.service.CreateDishUseCaseImpl;
import com.pragma.plazoleta.dish.application.service.GetPagedDishByRestaurantAndCategoryUseCaseImpl;
import com.pragma.plazoleta.dish.application.service.UpdateDishUseCaseImpl;
import com.pragma.plazoleta.dish.application.service.UpdateStatusDishUseCaseImpl;
import com.pragma.plazoleta.dish.domain.port.output.CategoryRepositoryPort;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DishBeanConfig {

    @Bean
    public CreateDishUseCase createDishUseCase(
            DishRepositoryPort dishRepository,
            CategoryRepositoryPort categoryRepository,
            RestaurantRepositoryPort restaurantRepository,
            OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort
    ) {
        return new CreateDishUseCaseImpl(
                dishRepository,
                categoryRepository,
                restaurantRepository,
                ownerOfRestaurantValidatorPort
        );
    }

    @Bean
    public UpdateDishUseCase updateDishUseCase(
            DishRepositoryPort dishRepository,
            OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort
    ) {
        return new UpdateDishUseCaseImpl(dishRepository, ownerOfRestaurantValidatorPort);
    }

    @Bean
    public UpdateStatusDishUseCase updateActiveOrInactiveDishUseCase(
            DishRepositoryPort dishRepository,
            OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort
    ) {
        return new UpdateStatusDishUseCaseImpl(dishRepository, ownerOfRestaurantValidatorPort);
    }

    @Bean
    public GetPagedDishByRestaurantAndCategoryUseCase getPagedDishByRestaurantAndCategoryUseCase(
            DishRepositoryPort dishRepository
    ) {
        return new GetPagedDishByRestaurantAndCategoryUseCaseImpl(dishRepository);
    }
}
