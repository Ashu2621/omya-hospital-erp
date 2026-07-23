package com.omya.hospital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class DoctorRequest {

  @NotBlank
  @Size(max = 160)
  private String doctorName;

  @NotBlank
  @Size(max = 120)
  private String specialization;

  @NotBlank
  @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must contain 10 digits")
  private String mobile;

  @NotBlank
  @Email
  @Size(max = 254)
  private String email;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal consultationFee;

  public String getDoctorName() {
    return doctorName;
  }

  public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BigDecimal getConsultationFee() {
    return consultationFee;
  }

  public void setConsultationFee(BigDecimal consultationFee) {
    this.consultationFee = consultationFee;
  }
}
