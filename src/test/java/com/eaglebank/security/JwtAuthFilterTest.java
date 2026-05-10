package com.eaglebank.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

  @Mock private JwtService jwtService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain chain;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldNotFilter_forPublicEndpoints() throws ServletException {
    JwtAuthFilter filter = new JwtAuthFilter(jwtService);

    when(request.getRequestURI()).thenReturn("/v1/auth/login");
    when(request.getMethod()).thenReturn("GET");
    assertThat(filter.shouldNotFilter(request)).isTrue();

    when(request.getRequestURI()).thenReturn("/v1/users");
    when(request.getMethod()).thenReturn("POST");
    assertThat(filter.shouldNotFilter(request)).isTrue();
  }

  @Test
  void doFilterInternal_setsAuthentication_whenValidToken() throws ServletException, IOException {
    JwtAuthFilter filter = new JwtAuthFilter(jwtService);

    when(request.getHeader("Authorization")).thenReturn("Bearer sometoken");
    when(jwtService.validate("sometoken")).thenReturn(true);
    when(jwtService.extractUsername("sometoken")).thenReturn("bob");

    filter.doFilterInternal(request, response, chain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getName()).isEqualTo("bob");
  }
}
