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
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DishServiceImplTest {

    @Mock
    private DishUseCase useCase;

    @Mock
    private DishResponseMapper responseMapper;

    @Mock
    private PaginationQueryMapper paginationQueryMapper;

    @Mock
    private PaginationResultMapper paginationResultMapper;

    @InjectMocks
    private DishServiceImpl dishService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDish_shouldReturnMappedDishResponse() {
        Long ownerId = 1L;
        CreateDishCommand command = new CreateDishCommand();
        Dish domainDish = new Dish();
        DishResponse expectedResponse = new DishResponse();

        when(responseMapper.toDomain(command)).thenReturn(domainDish);
        when(useCase.createDish(ownerId, domainDish)).thenReturn(domainDish);
        when(responseMapper.toResponse(domainDish)).thenReturn(expectedResponse);

        DishResponse response = dishService.createDish(ownerId, command);

        assertThat(response).isEqualTo(expectedResponse);
        verify(responseMapper).toDomain(command);
        verify(useCase).createDish(ownerId, domainDish);
        verify(responseMapper).toResponse(domainDish);
    }

    @Test
    void updateDish_shouldReturnMappedDishResponse() {
        Long ownerId = 1L;
        Long dishId = 10L;
        UpdateDishCommand command = new UpdateDishCommand();
        Dish domainDish = new Dish();
        DishResponse expectedResponse = new DishResponse();

        when(responseMapper.toDomain(command)).thenReturn(domainDish);
        when(useCase.updateDish(ownerId, dishId, domainDish)).thenReturn(domainDish);
        when(responseMapper.toResponse(domainDish)).thenReturn(expectedResponse);

        DishResponse response = dishService.updateDish(ownerId, dishId, command);

        assertThat(response).isEqualTo(expectedResponse);
        verify(responseMapper).toDomain(command);
        verify(useCase).updateDish(ownerId, dishId, domainDish);
        verify(responseMapper).toResponse(domainDish);
    }

    @Test
    void updateDishStatus_shouldReturnMappedDishResponse() {
        Long ownerId = 1L;
        Long dishId = 20L;
        UpdateStatusDishCommand command = new UpdateStatusDishCommand();
        Dish domainDish = new Dish();
        DishResponse expectedResponse = new DishResponse();

        when(responseMapper.toDomain(command)).thenReturn(domainDish);
        when(useCase.updateDishStatus(ownerId, dishId, domainDish)).thenReturn(domainDish);
        when(responseMapper.toResponse(domainDish)).thenReturn(expectedResponse);

        DishResponse response = dishService.updateDishStatus(ownerId, dishId, command);

        assertThat(response).isEqualTo(expectedResponse);
        verify(responseMapper).toDomain(command);
        verify(useCase).updateDishStatus(ownerId, dishId, domainDish);
        verify(responseMapper).toResponse(domainDish);
    }

    @Test
    void getPagedDishByRestaurantIdAndCategoryId_shouldReturnPageOfDishResponse() {
        Long restaurantId = 1L;
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        PaginationQuery paginationQuery = PaginationQuery.of(0, 10);
        Dish domainDish = new Dish();
        DishResponse dishResponse = new DishResponse();
        List<Dish> domainDishes = List.of(domainDish);
        PaginationResult<Dish> domainPagination = new PaginationResult<>(domainDishes, paginationQuery, 1L);
        PaginationResult<DishResponse> mappedPagination = new PaginationResult<>(List.of(dishResponse), paginationQuery, 1L);
        Page<DishResponse> expectedPage = new PageImpl<>(List.of(dishResponse), pageable, 1);

        when(paginationQueryMapper.toPaginationQuery(pageable)).thenReturn(paginationQuery);
        when(useCase.getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery))
                .thenReturn(domainPagination);
        when(responseMapper.toResponse(domainDish)).thenReturn(dishResponse);
        when(paginationResultMapper.toPage(mappedPagination)).thenReturn(expectedPage);

        Page<DishResponse> result = dishService.getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(paginationQueryMapper).toPaginationQuery(pageable);
        verify(useCase).getPagedDishByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery);
        verify(responseMapper).toResponse(domainDish);
        verify(paginationResultMapper).toPage(mappedPagination);
    }
}
