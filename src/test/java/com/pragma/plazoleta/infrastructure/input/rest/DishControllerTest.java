package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.CreateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateDishCommand;
import com.pragma.plazoleta.application.dto.request.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.service.DishService;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import com.pragma.plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.security.JwtAuthenticationRequestFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DishController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationRequestFilter.class)
})
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "OWNER")
    void createDish_shouldReturnCreated() throws Exception {
        CreateDishCommand command = new CreateDishCommand();
        DishResponse response = new DishResponse();
        Long ownerId = 1L;

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "owner@example.com", List.of("OWNER"));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of()));
        SecurityContextHolder.setContext(context);

        Mockito.when(dishService.createDish(eq(ownerId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/dishes")
                        .principal(ownerId::toString) // Simula ID como principal name
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateDish_shouldReturnOk() throws Exception {
        UpdateDishCommand command = new UpdateDishCommand();
        DishResponse response = new DishResponse();
        Long dishId = 1L;
        Long ownerId = 1L;

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "owner@example.com", List.of("OWNER"));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of()));
        SecurityContextHolder.setContext(context);

        Mockito.when(dishService.updateDish(eq(ownerId), eq(dishId), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/dishes/{dishId}", dishId)
                        .principal(ownerId::toString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void updateDishStatus_shouldReturnOk() throws Exception {
        UpdateStatusDishCommand command = new UpdateStatusDishCommand();
        DishResponse response = new DishResponse();
        Long dishId = 1L;
        Long ownerId = 1L;

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "owner@example.com", List.of("OWNER"));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of()));
        SecurityContextHolder.setContext(context);

        Mockito.when(dishService.updateDishStatus(eq(ownerId), eq(dishId), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/dishes/{dishId}/status", dishId)
                        .principal(ownerId::toString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPagedDishByRestaurantIdAndCategory_shouldReturnOk() throws Exception {
        Long restaurantId = 1L;
        Long categoryId = 2L;

        Mockito.when(dishService.getPagedDishByRestaurantIdAndCategoryId(
                        eq(restaurantId), eq(categoryId), any()))
                .thenReturn(new PageImpl<>(List.of(new DishResponse()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/dishes/restaurant/{restaurantId}/category/{categoryId}", restaurantId, categoryId))
                .andExpect(status().isOk());
    }
}
