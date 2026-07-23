package com.omya.hospital.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.security.jwt.secret}")
  private String rawSecret;

  @Value("${app.security.jwt.access-token-ttl}")
  private Duration accessTokenTtl;

  private SecretKey secretKey;

  @PostConstruct
  void initializeKey() {
    if (
      rawSecret == null ||
      rawSecret.getBytes(StandardCharsets.UTF_8).length < 32
    ) {
      throw new IllegalStateException(
        "JWT_SECRET must contain at least 32 bytes"
      );
    }
    secretKey = Keys.hmacShaKeyFor(rawSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String generate(String username, String role) {
    Instant now = Instant.now();
    return Jwts.builder()
      .subject(username)
      .claim("role", role)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plus(accessTokenTtl)))
      .signWith(secretKey)
      .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}
