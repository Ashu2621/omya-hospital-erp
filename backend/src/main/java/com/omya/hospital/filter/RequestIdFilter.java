package com.omya.hospital.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

  private static final String HEADER = "X-Request-Id";

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String requestId = Optional.ofNullable(request.getHeader(HEADER))
      .filter(value -> value.matches("^[A-Za-z0-9_-]{8,80}$"))
      .orElseGet(() -> UUID.randomUUID().toString());
    response.setHeader(HEADER, requestId);
    try (MDC.MDCCloseable ignored = MDC.putCloseable("traceId", requestId)) {
      filterChain.doFilter(request, response);
    }
  }
}
