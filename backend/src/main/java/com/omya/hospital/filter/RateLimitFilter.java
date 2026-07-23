package com.omya.hospital.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private record Window(long minute, AtomicInteger count) {}

  private final ConcurrentHashMap<String, Window> requests =
    new ConcurrentHashMap<>();
  private final Clock clock = Clock.systemUTC();

  @Value("${app.security.rate-limit.requests-per-minute}")
  private int limit;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    if (
      "OPTIONS".equals(request.getMethod()) ||
      request.getRequestURI().contains("/actuator/health")
    ) {
      filterChain.doFilter(request, response);
      return;
    }
    long minute = clock.millis() / 60_000L;
    String client = clientAddress(request);
    Window window = requests.compute(client, (key, current) ->
      current == null || current.minute() != minute
        ? new Window(minute, new AtomicInteger(1))
        : new Window(
            minute,
            new AtomicInteger(current.count().incrementAndGet())
          )
    );
    if (window.count().get() > limit) {
      response.setStatus(429);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setHeader("Retry-After", "60");
      response
        .getWriter()
        .write(
          "{\"status\":429,\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Try again in one minute.\"}"
        );
      return;
    }
    if (requests.size() > 10_000) {
      requests.entrySet().removeIf(entry -> entry.getValue().minute() < minute);
    }
    filterChain.doFilter(request, response);
  }

  private String clientAddress(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    return forwarded == null || forwarded.isBlank()
      ? request.getRemoteAddr()
      : forwarded.split(",")[0].trim();
  }
}
