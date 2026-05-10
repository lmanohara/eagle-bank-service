package com.eaglebank.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    // Skip filtering for public auth endpoints and user creation
    if (path.startsWith("/v1/auth")) return true;
    if ("/v1/users".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) return true;

    return false;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      if (jwtService.validate(token)) {
        String username = jwtService.extractUsername(token);

        var auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }

    filterChain.doFilter(request, response);
  }
}
