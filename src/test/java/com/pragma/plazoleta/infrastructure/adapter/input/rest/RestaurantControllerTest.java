package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.RestaurantResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.handler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.adapter.input.security.JwtAuthenticationRequestFilter;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantMapper;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationRequestFilter.class)
})
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateRestaurantUseCase createRestaurantUseCase;

    @MockitoBean
    private RestaurantMapper restaurantMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1/restaurants";

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldReturn201WhenRestaurantCreated() throws Exception {
        // given
        CreateRestaurantCommand request = new CreateRestaurantCommand("Resto", "Dir", "+51999999999", "logo", "123456", 1L);
        Restaurant restaurant = Restaurant.builder().id(1L).name("Resto").build();
        RestaurantResponse response = new RestaurantResponse(1L, "Resto", "Dir", "+51999999999", "logo", "123456", 1L);

        when(createRestaurantUseCase.createRestaurant(any())).thenReturn(restaurant);
        when(restaurantMapper.toResponse(restaurant)).thenReturn(response);

        // when - then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Resto"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldReturn400WhenOwnerIsInvalid() throws Exception {
        // given
        CreateRestaurantCommand request = new CreateRestaurantCommand("Resto", "Dir", "123", "logo", "123456", 999L);
        when(createRestaurantUseCase.createRestaurant(any()))
                .thenThrow(new InvalidOwnerException("El usuario no tiene el rol OWNER"));

        // when - then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Propietario inválido"))
                .andExpect(jsonPath("$.message").value("El usuario no tiene el rol OWNER"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldReturn500OnGenericException() throws Exception {
        // given
        CreateRestaurantCommand request = new CreateRestaurantCommand("Resto", "Dir", "123", "logo", "123456", 1L);
        when(createRestaurantUseCase.createRestaurant(any()))
                .thenThrow(new RuntimeException("Error interno"));

        // when - then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno"))
                .andExpect(jsonPath("$.message").value("Error interno"));
    }
}
