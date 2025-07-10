package com.pragma.plazoleta.auth.infrastructure.adapter.input.security;

import com.pragma.plazoleta.auth.domain.model.AuthenticatedUser;
import com.pragma.plazoleta.auth.domain.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationRequestFilterTest {

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private JwtAuthenticationRequestFilter filter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {
        // given
        String token = "valid.jwt.token";
        String email = "user@example.com";
        List<String> roles = List.of("ROLE_OWNER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);

        when(authenticatedUser.getEmail()).thenReturn(email);
        when(authenticatedUser.getRoles()).thenReturn(roles);
        when(tokenProvider.getAuthenticatedUser(token)).thenReturn(authenticatedUser);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        AuthenticatedUser actualUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(email, actualUser.getEmail());
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(tokenProvider.getAuthenticatedUser("invalid-token")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
