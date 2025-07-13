package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.domain.exception.ValidationException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.domain.validation.Validation;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class RestaurantUseCaseImpl implements RestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;

    /**
     * Requisitos de validación:
     * 1. La creación de un restaurante requiere los campos obligatorios:
     * nombre, NIT, dirección, teléfono, url del logo y el ID del propietario.
     * 2. Validar que el ID del usuario tenga el rol OWNER.
     * 3. NIT y teléfono deben ser únicamente numéricos. Teléfono máx. 13 y puede tener '+'.
     * 4. El nombre no puede ser solo números.
     */
    public void validateCreateRestaurant(Restaurant restaurant) throws ValidationException, BusinessLogicException {
        // Validaciones estructurales y de formato: Req 1, 3 y 4
        Validation.builder(restaurant)
                .notBlank("name", Restaurant::getName)
                .notMatch("name", Restaurant::getName, "^\\d+$")
                .notBlank("address", Restaurant::getAddress)
                .notBlank("phone", Restaurant::getPhone)
                .pattern("phone", Restaurant::getPhone, "^\\+?[0-9]{1,13}$")
                .notBlank("logoUrl", Restaurant::getLogoUrl)
                .notBlank("nit", Restaurant::getNit)
                .pattern("nit", Restaurant::getNit, "^\\d+$")
                .notNull("ownerId", Restaurant::getOwnerId)
                .positive("ownerId", Restaurant::getOwnerId)
                .build()
                .validate();
        // Req 2: Validar que el usuario tenga el rol OWNER
        if (!ownerValidatorPort.isOwner(restaurant.getOwnerId())) {
            throw new BusinessLogicException("El usuario no tiene el rol OWNER");
        }
    }

    @Override
    public Restaurant createRestaurant(Restaurant command) {
        validateCreateRestaurant(command);
        return restaurantRepositoryPort.save(command);
    }

    @Override
    public PaginationResult<Restaurant> findAllPaginated(PaginationQuery query) {
        return restaurantRepositoryPort.findAllPaged(query);
    }
}
