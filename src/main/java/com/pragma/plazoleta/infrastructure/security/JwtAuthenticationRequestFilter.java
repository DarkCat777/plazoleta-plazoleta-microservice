package com.pragma.plazoleta.infrastructure.security;

import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import com.pragma.plazoleta.domain.spi.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationRequestFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractToken(request);

        if (authHeader == null || token == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Bearer token found in the request header.");
            chain.doFilter(request, response);
            return;
        }

        AuthenticatedUser authenticatedUser = tokenProvider.decodeToken(token);

        if (authenticatedUser == null || authenticatedUser.getEmail() == null || authenticatedUser.getEmail().isBlank()) {
            log.error("Invalid token: {}", token);
            chain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(authenticatedUser.getEmail())
                .password("N/A")
                .authorities(authenticatedUser.getRoles().stream().map(SimpleGrantedAuthority::new).toList())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication successful for user: {}", authenticatedUser.getEmail());

        chain.doFilter(request, response);
    }
}
