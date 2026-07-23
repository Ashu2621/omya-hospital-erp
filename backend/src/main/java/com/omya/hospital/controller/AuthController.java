package com.omya.hospital.controller;

import com.omya.hospital.dto.LoginRequest;
import com.omya.hospital.dto.LoginResponse;
import com.omya.hospital.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @Valid @RequestBody LoginRequest request
  ) {
    return ResponseEntity.ok(authService.login(request));
  }
}
