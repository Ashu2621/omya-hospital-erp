package com.omya.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

  @NotBlank(message = "Username is required")
  @Size(max = 100, message = "Username is too long")
  private String username;

  @NotBlank(message = "Password is required")
  @Size(max = 200, message = "Password is too long")
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
