package com.pragma.plazoleta.application.service.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.request.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.application.service.RestaurantService;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantUseCase useCase;
    private final RestaurantResponseMapper responseMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantCommand request) {
        return responseMapper.toResponse(useCase.createRestaurant(responseMapper.toDomain(request)));
    }

    @Override
    public Page<RestaurantItemPageResponse> findAllPaginated(Pageable pageable) {
        PaginationQuery paginationQuery = paginationQueryMapper.toPaginationQuery(pageable);
        PaginationResult<RestaurantItemPageResponse> paginationResult = useCase.findAllPaginated(paginationQuery)
                .map(responseMapper::toItemPageResponse);
        return paginationResultMapper.toPage(paginationResult);
    }
}
