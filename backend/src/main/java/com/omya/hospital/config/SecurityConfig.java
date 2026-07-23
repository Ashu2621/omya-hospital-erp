package com.omya.hospital.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omya.hospital.filter.JwtFilter;
import com.omya.hospital.filter.RateLimitFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  private final JwtFilter jwtFilter;
  private final RateLimitFilter rateLimitFilter;
  private final ObjectMapper objectMapper;

  @Value("${app.security.cors.allowed-origins}")
  private String allowedOrigins;

  public SecurityConfig(
    JwtFilter jwtFilter,
    RateLimitFilter rateLimitFilter,
    ObjectMapper objectMapper
  ) {
    this.jwtFilter = jwtFilter;
    this.rateLimitFilter = rateLimitFilter;
    this.objectMapper = objectMapper;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(auth ->
        auth
          .requestMatchers(
            "/auth/**",
            "/health",
            "/actuator/health/**",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
          )
          .permitAll()
          .requestMatchers("/dashboard/**", "/audit/**", "/doctor/**")
          .hasRole("ADMIN")
          .requestMatchers("/patient/**", "/appointment/**")
          .hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")
          .requestMatchers("/medicine/**", "/billing/**")
          .hasAnyRole("ADMIN", "PHARMACIST")
          .requestMatchers("/lab/**")
          .hasAnyRole("ADMIN", "LAB_TECHNICIAN", "DOCTOR")
          .requestMatchers("/prescription/**")
          .hasAnyRole("ADMIN", "DOCTOR")
          .requestMatchers("/admission/**")
          .hasAnyRole("ADMIN", "NURSE", "DOCTOR")
          .requestMatchers("/subscription/**")
          .hasAnyRole("ADMIN", "RECEPTIONIST")
          .requestMatchers("/actuator/metrics/**")
          .hasRole("ADMIN")
          .anyRequest()
          .authenticated()
      )
      .exceptionHandling(errors ->
        errors
          .authenticationEntryPoint((request, response, exception) ->
            writeSecurityError(response, 401, "AUTHENTICATION_REQUIRED")
          )
          .accessDeniedHandler((request, response, exception) ->
            writeSecurityError(response, 403, "ACCESS_DENIED")
          )
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(rateLimitFilter, JwtFilter.class)
      .httpBasic(basic -> basic.disable())
      .formLogin(form -> form.disable())
      .build();
  }

  @Bean
  FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter filter) {
    FilterRegistrationBean<JwtFilter> registration =
      new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(
    RateLimitFilter filter
  ) {
    FilterRegistrationBean<RateLimitFilter> registration =
      new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
      .map(String::trim)
      .filter(origin -> !origin.isBlank())
      .toList();
    configuration.setAllowedOrigins(origins);
    configuration.setAllowedMethods(
      List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    );
    configuration.setAllowedHeaders(
      List.of(
        "Authorization",
        "Content-Type",
        "Idempotency-Key",
        "X-Request-Id"
      )
    );
    configuration.setExposedHeaders(List.of("X-Request-Id"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private void writeSecurityError(
    HttpServletResponse response,
    int status,
    String code
  ) throws java.io.IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
      response.getWriter(),
      java.util.Map.of(
        "status",
        status,
        "code",
        code,
        "message",
        status == 401
          ? "Valid authentication is required."
          : "You do not have permission for this action."
      )
    );
  }
}
