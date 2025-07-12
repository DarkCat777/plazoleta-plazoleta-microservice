package com.pragma.plazoleta.application.service.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.request.CreateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.mapper.DishResponseMapper;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.application.service.DishService;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishUseCase useCase;
    private final DishResponseMapper responseMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public DishResponse createDish(Long ownerId, CreateDishCommand command) {
        return responseMapper.toResponse(useCase.createDish(ownerId, responseMapper.toDomain(command)));
    }

    @Override
    public DishResponse updateDish(Long ownerId, Long dishId, UpdateDishCommand command) {
        return responseMapper.toResponse(useCase.updateDish(ownerId, dishId, responseMapper.toDomain(command)));
    }

    @Override
    public DishResponse updateDishStatus(Long ownerId, Long dishId, UpdateStatusDishCommand command) {
        return responseMapper.toResponse(useCase.updateDishStatus(ownerId, dishId, responseMapper.toDomain(command)));
    }

    @Override
    public Page<DishResponse> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, Pageable pageable) {
        PaginationQuery paginationQuery = paginationQueryMapper.toPaginationQuery(pageable);
        PaginationResult<DishResponse> paginationResult = useCase.getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery)
                .map(responseMapper::toResponse);
        return paginationResultMapper.toPage(paginationResult);
    }
}
