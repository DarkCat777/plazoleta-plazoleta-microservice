package com.pragma.plazoleta.application.service.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.request.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceImplTest {

    private RestaurantUseCase restaurantUseCase;
    private RestaurantResponseMapper responseMapper;
    private PaginationQueryMapper paginationQueryMapper;
    private PaginationResultMapper paginationResultMapper;

    private RestaurantServiceImpl restaurantService;

    @BeforeEach
    void setUp() {
        restaurantUseCase = mock(RestaurantUseCase.class);
        responseMapper = mock(RestaurantResponseMapper.class);
        paginationQueryMapper = mock(PaginationQueryMapper.class);
        paginationResultMapper = mock(PaginationResultMapper.class);

        restaurantService = new RestaurantServiceImpl(
                restaurantUseCase, responseMapper, paginationQueryMapper, paginationResultMapper
        );
    }

    @Test
    void createRestaurant_shouldReturnResponse() {
        CreateRestaurantCommand command = new CreateRestaurantCommand();
        Restaurant domain = new Restaurant();
        RestaurantResponse expectedResponse = new RestaurantResponse();

        when(responseMapper.toDomain(command)).thenReturn(domain);
        when(restaurantUseCase.createRestaurant(domain)).thenReturn(domain);
        when(responseMapper.toResponse(domain)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.createRestaurant(command);

        assertEquals(expectedResponse, result);
        verify(responseMapper).toDomain(command);
        verify(restaurantUseCase).createRestaurant(domain);
        verify(responseMapper).toResponse(domain);
    }

    @Test
    void findAllPaginated_shouldReturnPagedResponse() {
        var pageable = PageRequest.of(0, 10);
        var paginationQuery = PaginationQuery.of(0, 10);
        var domainRestaurant = new Restaurant();
        var itemPageResponse = new RestaurantItemPageResponse();
        var paginationResult = new PaginationResult<>(
                List.of(itemPageResponse), paginationQuery, 1L
        );
        Page<RestaurantItemPageResponse> expectedPage = new PageImpl<>(List.of(itemPageResponse), pageable, 1);

        when(paginationQueryMapper.toPaginationQuery(pageable)).thenReturn(paginationQuery);
        when(restaurantUseCase.findAllPaginated(paginationQuery))
                .thenReturn(new PaginationResult<>(List.of(domainRestaurant), paginationQuery, 1L));
        when(responseMapper.toItemPageResponse(domainRestaurant)).thenReturn(itemPageResponse);
        when(paginationResultMapper.toPage(paginationResult)).thenReturn(expectedPage);

        Page<RestaurantItemPageResponse> result = restaurantService.findAllPaginated(pageable);

        assertEquals(expectedPage, result);
        verify(paginationQueryMapper).toPaginationQuery(pageable);
        verify(restaurantUseCase).findAllPaginated(paginationQuery);
        verify(responseMapper).toItemPageResponse(domainRestaurant);
        verify(paginationResultMapper).toPage(paginationResult);
    }
}
