package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.CreateRestaurantCommand;
import com.pragma.plazoleta.application.dto.response.RestaurantItemPageResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.mapper.RestaurantResponseMapper;
import com.pragma.plazoleta.application.service.RestaurantService;
import com.pragma.plazoleta.config.TestSecurityConfig;
import com.pragma.plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import com.pragma.plazoleta.infrastructure.security.JwtAuthenticationRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationRequestFilter.class)
})
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private RestaurantResponseMapper restaurantMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void createRestaurant_shouldReturnCreated() throws Exception {
        CreateRestaurantCommand request = new CreateRestaurantCommand();
        RestaurantResponse response = new RestaurantResponse();

        when(restaurantService.createRestaurant(any(CreateRestaurantCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPaginatedRestaurants_shouldReturnPage() throws Exception {
        RestaurantItemPageResponse item = new RestaurantItemPageResponse();
        Pageable pageable = PageRequest.of(0, 10);

        when(restaurantService.findAllPaginated(any()))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
