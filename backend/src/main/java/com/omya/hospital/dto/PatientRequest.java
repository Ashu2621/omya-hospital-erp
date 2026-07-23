package com.omya.hospital.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientRequest {

  @NotBlank
  @Size(max = 160)
  private String patientName;

  @NotNull
  @Min(0)
  @Max(120)
  private Integer age;

  @NotBlank
  @Size(max = 30)
  private String gender;

  @NotBlank
  @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must contain 10 digits")
  private String mobile;

  @Size(max = 500)
  private String address;

  public String getPatientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
