package com.omya.hospital.service;

import com.omya.hospital.dto.LoginRequest;
import com.omya.hospital.dto.LoginResponse;
import com.omya.hospital.exception.ApiException;
import com.omya.hospital.repository.UserRepository;
import com.omya.hospital.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private static final String INVALID_CREDENTIALS =
    "Invalid username or password.";

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public AuthService(
    UserRepository userRepository,
    PasswordEncoder encoder,
    JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  public LoginResponse login(LoginRequest request) {
    var user = userRepository
      .findByUsername(request.getUsername())
      .orElseThrow(() ->
        new ApiException(
          HttpStatus.UNAUTHORIZED,
          "INVALID_CREDENTIALS",
          INVALID_CREDENTIALS
        )
      );
    if (
      !Boolean.TRUE.equals(user.getActive()) ||
      !encoder.matches(request.getPassword(), user.getPasswordHash())
    ) {
      throw new ApiException(
        HttpStatus.UNAUTHORIZED,
        "INVALID_CREDENTIALS",
        INVALID_CREDENTIALS
      );
    }
    String role = user.getRole().getRoleName();
    String token = jwtService.generate(user.getUsername(), role);
    return new LoginResponse("LOGIN_SUCCESS", role, token);
  }
}
