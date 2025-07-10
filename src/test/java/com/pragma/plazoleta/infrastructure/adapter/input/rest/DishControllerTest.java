package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.application.port.input.GetPagedDishByRestaurantAndCategoryUseCase;
import com.pragma.plazoleta.application.port.input.UpdateStatusDishUseCase;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.application.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.CategoryResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.DishResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.handler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.adapter.input.security.JwtAuthenticationRequestFilter;
import com.pragma.plazoleta.infrastructure.adapter.mapper.DishResponseMapper;
import com.pragma.plazoleta.infrastructure.adapter.mapper.PaginationQueryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DishController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationRequestFilter.class)
})
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateDishUseCase createDishUseCase;

    @MockitoBean
    private UpdateDishUseCase updateDishUseCase;

    @MockitoBean
    private UpdateStatusDishUseCase updateStatusDishUseCase;

    @MockitoBean
    private GetPagedDishByRestaurantAndCategoryUseCase getPagedDishByRestaurantAndCategoryUseCase;

    @MockitoBean
    private DishResponseMapper dishMapper;

    @MockitoBean
    private PaginationQueryMapper paginationQueryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldCreateDishSuccessfully() throws Exception {
        CreateDishCommand command = new CreateDishCommand();
        command.setName("Pizza");
        command.setPrice(25000);
        command.setDescription("Pizza napolitana");
        command.setImageUrl("https://image.com/pizza.jpg");
        command.setCategoryId(1L);
        command.setRestaurantId(1L);

        Dish domainDish = Dish.builder()
                .id(10L)
                .name("Pizza")
                .price(25000)
                .description("Pizza napolitana")
                .imageUrl("https://image.com/pizza.jpg")
                .active(true)
                .build();

        DishResponse response = new DishResponse(
                10L, "Pizza", 25000, "Pizza napolitana", "https://image.com/pizza.jpg", true, null, null
        );

        when(createDishUseCase.createDish(any())).thenReturn(domainDish);
        when(dishMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.price").value(25000))
                .andExpect(jsonPath("$.description").value("Pizza napolitana"))
                .andExpect(jsonPath("$.imageUrl").value("https://image.com/pizza.jpg"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturn400WhenOwnerIsInvalid() throws Exception {
        CreateDishCommand command = new CreateDishCommand("Plato", 1500, "desc", "img", 1L, 1L);

        when(createDishUseCase.createDish(any())).thenThrow(new InvalidOwnerException("No autorizado"));

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Propietario inválido"));
    }

    @Test
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        CreateDishCommand command = new CreateDishCommand("Plato", 1500, "desc", "img", 1L, 1L);

        when(createDishUseCase.createDish(any())).thenThrow(new CategoryNotFoundException(1L));

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Categoría no encontrada"));
    }

    @Test
    void shouldReturn404WhenRestaurantNotFound() throws Exception {
        CreateDishCommand command = new CreateDishCommand("Plato", 1500, "desc", "img", 1L, 1L);

        when(createDishUseCase.createDish(any())).thenThrow(new RestaurantNotFoundException(1L));

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Restaurante no encontrado"));
    }

    @Test
    void shouldUpdateDishSuccessfully() throws Exception {
        Long dishId = 1L;
        UpdateDishCommand command = new UpdateDishCommand(1800, "Nueva descripción");
        Dish dish = Dish.builder().id(dishId).description("Nueva descripción").price(1800).build();
        DishResponse response = new DishResponse(dishId, "Plato", 1800, "Nueva descripción", "url", true, 1L, new CategoryResponse());

        when(updateDishUseCase.updateDish(eq(dishId), any())).thenReturn(dish);
        when(dishMapper.toResponse(dish)).thenReturn(response);

        mockMvc.perform(put("/api/v1/dishes/{dishId}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.description").value("Nueva descripción"))
                .andExpect(jsonPath("$.price").value(1800));
    }

    @Test
    void shouldReturn404WhenDishNotFound() throws Exception {
        Long dishId = 1L;
        UpdateDishCommand command = new UpdateDishCommand(1800, "Nueva descripción");

        when(updateDishUseCase.updateDish(eq(dishId), any()))
                .thenThrow(new DishNotFoundException(dishId));

        mockMvc.perform(put("/api/v1/dishes/{dishId}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Plato no encontrado"));
    }

    @Test
    void shouldReturn400WhenNotOwner() throws Exception {
        Long dishId = 1L;
        UpdateDishCommand command = new UpdateDishCommand(1800, "Nueva descripción");

        when(updateDishUseCase.updateDish(eq(dishId), any()))
                .thenThrow(new InvalidOwnerException("No autorizado"));

        mockMvc.perform(put("/api/v1/dishes/{dishId}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Propietario inválido"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldUpdateDishStatusSuccessfully() throws Exception {
        Long dishId = 5L;
        UpdateStatusDishCommand command = new UpdateStatusDishCommand();
        command.setActive(false);

        Dish updatedDish = Dish.builder()
                .id(dishId)
                .name("Sopa de verduras")
                .price(10000)
                .description("Sopa casera")
                .imageUrl("https://img.com/sopa.jpg")
                .active(false)
                .build();

        DishResponse response = new DishResponse(
                dishId, "Sopa de verduras", 10000, "Sopa casera", "https://img.com/sopa.jpg", false, 1L, new CategoryResponse()
        );

        when(updateStatusDishUseCase.updateDishStatus(eq(dishId), any()))
                .thenReturn(updatedDish);
        when(dishMapper.toResponse(updatedDish)).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/dishes/{dishId}/status", dishId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(command))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldReturn404WhenUpdatingNonExistentDishStatus() throws Exception {
        Long dishId = 999L;
        UpdateStatusDishCommand command = new UpdateStatusDishCommand();
        command.setActive(true);

        when(updateStatusDishUseCase.updateDishStatus(eq(dishId), any()))
                .thenThrow(new DishNotFoundException(dishId));

        mockMvc.perform(
                        put("/api/v1/dishes/{dishId}/status", dishId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(command))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Plato no encontrado"))
                .andExpect(jsonPath("$.message").value("No existe el plato con el ID: " + dishId));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnPagedDishesByRestaurantAndCategorySuccessfully() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        Long categoryId = 2L;
        int page = 0;
        int size = 2;

        CategoryResponse category = new CategoryResponse(categoryId, "Tradicional", "Comida típica");

        Dish dish1 = Dish.builder().id(10L).name("Lomo Saltado").build();
        Dish dish2 = Dish.builder().id(11L).name("Ceviche").build();

        DishResponse response1 = new DishResponse(10L, "Lomo Saltado", 25, "Desc1", "img1", true, restaurantId, category);
        DishResponse response2 = new DishResponse(11L, "Ceviche", 22, "Desc2", "img2", true, restaurantId, category);

        PaginationResult<Dish> domainResult = new PaginationResult<>(
                List.of(dish1, dish2), page, size, 2L, 1
        );

        // Mocks
        when(paginationQueryMapper.toPaginationQuery(any(Pageable.class)))
                .thenReturn(PaginationQuery.of(page, size));
        when(getPagedDishByRestaurantAndCategoryUseCase
                .getPagedDishByRestaurantIdAndCategoryId(eq(restaurantId), eq(categoryId), any(PaginationQuery.class)))
                .thenReturn(domainResult);
        when(dishMapper.toResponse(dish1)).thenReturn(response1);
        when(dishMapper.toResponse(dish2)).thenReturn(response2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dishes/restaurant/{restaurantId}/category/{categoryId}", restaurantId, categoryId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("Lomo Saltado"))
                .andExpect(jsonPath("$.content[0].price").value(25))
                .andExpect(jsonPath("$.content[0].description").value("Desc1"))
                .andExpect(jsonPath("$.content[0].imageUrl").value("img1"))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.content[0].restaurantId").value(restaurantId.intValue()))
                .andExpect(jsonPath("$.content[0].category.id").value(categoryId))
                .andExpect(jsonPath("$.content[0].category.name").value("Tradicional"))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }


}
