package com.omya.hospital.filter;

import com.omya.hospital.repository.UserRepository;
import com.omya.hospital.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      Claims claims = jwtService.parse(header.substring(7));
      String username = claims.getSubject();
      String role = claims.get("role", String.class);
      boolean active = userRepository
        .findByUsername(username)
        .map(user -> Boolean.TRUE.equals(user.getActive()))
        .orElse(false);
      if (
        active && SecurityContextHolder.getContext().getAuthentication() == null
      ) {
        var authentication = new UsernamePasswordAuthenticationToken(
          username,
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        authentication.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ignored) {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }
}
