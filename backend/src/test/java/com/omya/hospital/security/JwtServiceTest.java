package com.omya.hospital.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(
      jwtService,
      "rawSecret",
      "test-secret-that-is-longer-than-thirty-two-bytes"
    );
    ReflectionTestUtils.setField(
      jwtService,
      "accessTokenTtl",
      Duration.ofMinutes(30)
    );
    jwtService.initializeKey();
  }

  @Test
  void generatesTokenWithExpectedSubject() {
    String token = jwtService.generate("ashutosh", "ADMIN");
    assertThat(jwtService.parse(token).getSubject()).isEqualTo("ashutosh");
  }

  @Test
  void preservesRoleClaim() {
    String token = jwtService.generate("ashutosh", "PHARMACIST");
    assertThat(jwtService.parse(token).get("role", String.class)).isEqualTo(
      "PHARMACIST"
    );
  }

  @Test
  void setsExpirationAfterIssueTime() {
    String token = jwtService.generate("ashutosh", "ADMIN");
    var claims = jwtService.parse(token);
    assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
  }

  @Test
  void rejectsTamperedToken() {
    String token = jwtService.generate("ashutosh", "ADMIN");
    String tampered = token.substring(0, token.length() - 2) + "aa";
    assertThatThrownBy(() -> jwtService.parse(tampered)).isInstanceOf(
      RuntimeException.class
    );
  }

  @Test
  void rejectsShortSecretAtStartup() {
    JwtService invalid = new JwtService();
    ReflectionTestUtils.setField(invalid, "rawSecret", "too-short");
    ReflectionTestUtils.setField(
      invalid,
      "accessTokenTtl",
      Duration.ofMinutes(30)
    );
    assertThatThrownBy(invalid::initializeKey)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("32 bytes");
  }
}
