package com.pragma.plazoleta.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.response.RestaurantResponse;
import com.pragma.plazoleta.infrastructure.adapter.input.rest.handler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.adapter.input.security.JwtAuthenticationRequestFilter;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantResponseMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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
    private GetPagedRestaurantUseCase getPagedRestaurantUseCase;

    @MockitoBean
    private RestaurantResponseMapper restaurantMapper;

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

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnPaginatedRestaurantsSuccessfully() throws Exception {
        int page = 0;
        int size = 2;

        Restaurant r1 = Restaurant.builder().id(1L).name("Resto 1").build();
        Restaurant r2 = Restaurant.builder().id(2L).name("Resto 2").build();

        RestaurantItemPageResponse res1 = new RestaurantItemPageResponse(1L, "Resto 1", "img1");
        RestaurantItemPageResponse res2 = new RestaurantItemPageResponse(2L, "Resto 2", "img2");

        PaginatedResult<Restaurant> domainResult = new PaginatedResult<>(List.of(r1, r2), page, size, 2L, 1);

        when(getPagedRestaurantUseCase.findAllPaginated(PaginationQuery.of(page, size))).thenReturn(domainResult);
        when(restaurantMapper.toItemPageResponse(r1)).thenReturn(res1);
        when(restaurantMapper.toItemPageResponse(r2)).thenReturn(res2);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].name").value("Resto 2"));
    }
}
