package com.pragma.plazoleta.restaurant.infrastructure.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.restaurant.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.RestaurantController;
import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.restaurant.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.shared.config.TestSecurityConfig;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.input.rest.response.RestaurantResponse;
import com.pragma.plazoleta.shared.exception.GlobalExceptionHandler;
import com.pragma.plazoleta.auth.infrastructure.adapter.input.security.JwtAuthenticationRequestFilter;
import com.pragma.plazoleta.shared.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.mapper.RestaurantResponseMapper;
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

    @MockitoBean
    private PaginationQueryMapper paginationQueryMapper;

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

        PaginationResult<Restaurant> domainResult = new PaginationResult<>(
                List.of(r1, r2), page, size, 2L, 1
        );

        PaginationQuery paginationQuery = PaginationQuery.of(page, size);

        when(paginationQueryMapper.toPaginationQuery(any(Pageable.class))).thenReturn(paginationQuery);
        when(getPagedRestaurantUseCase.findAllPaginated(any(PaginationQuery.class)))
                .thenReturn(domainResult);
        when(restaurantMapper.toItemPageResponse(r1)).thenReturn(res1);
        when(restaurantMapper.toItemPageResponse(r2)).thenReturn(res2);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Resto 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Resto 2"))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

}
