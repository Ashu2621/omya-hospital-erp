package com.omya.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.omya.hospital.dto.LoginRequest;
import com.omya.hospital.entity.Role;
import com.omya.hospital.entity.User;
import com.omya.hospital.exception.ApiException;
import com.omya.hospital.repository.UserRepository;
import com.omya.hospital.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

  private UserRepository users;
  private PasswordEncoder encoder;
  private JwtService tokens;
  private AuthService service;

  @BeforeEach
  void setUp() {
    users = mock(UserRepository.class);
    encoder = mock(PasswordEncoder.class);
    tokens = mock(JwtService.class);
    service = new AuthService(users, encoder, tokens);
  }

  @Test
  void logsInActiveUserWithValidPassword() {
    User user = user("admin", true, "ADMIN");
    when(users.findByUsername("admin")).thenReturn(Optional.of(user));
    when(encoder.matches("strong-password", "hash")).thenReturn(true);
    when(tokens.generate("admin", "ADMIN")).thenReturn("signed-token");

    var response = service.login(request("admin", "strong-password"));

    assertThat(response.getToken()).isEqualTo("signed-token");
    assertThat(response.getRole()).isEqualTo("ADMIN");
    verify(tokens).generate("admin", "ADMIN");
  }

  @Test
  void rejectsUnknownUserWithGenericMessage() {
    when(users.findByUsername("missing")).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.login(request("missing", "wrong")))
      .isInstanceOf(ApiException.class)
      .hasMessage("Invalid username or password.");
  }

  @Test
  void rejectsWrongPasswordWithSameGenericMessage() {
    when(users.findByUsername("admin")).thenReturn(
      Optional.of(user("admin", true, "ADMIN"))
    );
    when(encoder.matches("wrong", "hash")).thenReturn(false);
    assertThatThrownBy(() -> service.login(request("admin", "wrong")))
      .isInstanceOf(ApiException.class)
      .hasMessage("Invalid username or password.");
  }

  @Test
  void rejectsInactiveUser() {
    when(users.findByUsername("admin")).thenReturn(
      Optional.of(user("admin", false, "ADMIN"))
    );
    assertThatThrownBy(() ->
      service.login(request("admin", "strong-password"))
    ).isInstanceOf(ApiException.class);
  }

  private LoginRequest request(String username, String password) {
    LoginRequest request = new LoginRequest();
    request.setUsername(username);
    request.setPassword(password);
    return request;
  }

  private User user(String username, boolean active, String roleName) {
    Role role = new Role();
    role.setRoleName(roleName);
    User user = new User();
    user.setUsername(username);
    user.setActive(active);
    user.setPasswordHash("hash");
    user.setRole(role);
    return user;
  }
}
