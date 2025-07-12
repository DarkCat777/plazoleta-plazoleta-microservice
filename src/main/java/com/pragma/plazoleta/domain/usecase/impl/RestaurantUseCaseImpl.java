package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pragma.plazoleta.domain.validation.ValidationUtils.*;

@RequiredArgsConstructor
public class RestaurantUseCaseImpl implements RestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;

    private void validateCreateRestaurant(Restaurant command) {
        List<FieldValidationError> errors = new ArrayList<>();

        errors.add(validateNotBlank("name", command.getName()));
        errors.add(validateNotOnlyNumbers("name", command.getName()));

        errors.add(validateNotBlank("address", command.getAddress()));

        errors.add(validateNotBlank("phone", command.getPhone()));
        errors.add(validatePhone("phone", command.getPhone()));

        errors.add(validateNotBlank("logoUrl", command.getLogoUrl()));

        errors.add(validateNotBlank("nit", command.getNit()));
        errors.add(validateOnlyNumbers("nit", command.getNit()));

        errors.add(validateNotNull("ownerId", command.getOwnerId()));

        errors.removeIf(Objects::isNull);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Override
    public Restaurant createRestaurant(Restaurant command) {
        if (!ownerValidatorPort.isOwner(command.getOwnerId())) {
            throw new InvalidOwnerException("El usuario no tiene el rol OWNER");
        }
        Restaurant restaurant = Restaurant.builder()
                .name(command.getName())
                .address(command.getAddress())
                .phone(command.getPhone())
                .logoUrl(command.getLogoUrl())
                .nit(command.getNit())
                .ownerId(command.getOwnerId())
                .build();
        return restaurantRepositoryPort.save(restaurant);
    }

    @Override
    public PaginationResult<Restaurant> findAllPaginated(PaginationQuery query) {
        return restaurantRepositoryPort.findAllPaged(query);
    }
}
