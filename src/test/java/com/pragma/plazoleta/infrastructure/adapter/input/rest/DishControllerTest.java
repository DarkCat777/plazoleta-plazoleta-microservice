package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.dto.UpdateActiveOrInactiveCommand;
import com.pragma.plazoleta.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.application.port.input.UpdateActiveOrInactiveDishUseCase;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.application.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.CategoryResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.DishResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.handler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.adapter.input.security.JwtAuthenticationRequestFilter;
import com.pragma.plazoleta.infrastructure.adapter.mapper.DishResponseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private UpdateActiveOrInactiveDishUseCase updateActiveOrInactiveDishUseCase;

    @MockitoBean
    private DishResponseMapper dishMapper;

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
        UpdateActiveOrInactiveCommand command = new UpdateActiveOrInactiveCommand();
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

        when(updateActiveOrInactiveDishUseCase.updateDishActiveOrInactive(eq(dishId), any()))
                .thenReturn(updatedDish);
        when(dishMapper.toResponse(updatedDish)).thenReturn(response);

        mockMvc.perform(
                        patch("/api/v1/dishes/{dishId}/status", dishId)
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
        UpdateActiveOrInactiveCommand command = new UpdateActiveOrInactiveCommand();
        command.setActive(true);

        when(updateActiveOrInactiveDishUseCase.updateDishActiveOrInactive(eq(dishId), any()))
                .thenThrow(new DishNotFoundException(dishId));

        mockMvc.perform(
                        patch("/api/v1/dishes/{dishId}/status", dishId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(command))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Plato no encontrado"))
                .andExpect(jsonPath("$.message").value("No existe el plato con el ID: " + dishId));
    }

}
