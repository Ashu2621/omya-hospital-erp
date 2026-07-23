package com.omya.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AdmissionRequest {

  @NotNull
  @Positive
  private Long patientId;

  @NotNull
  @Positive
  private Long doctorId;

  @NotBlank
  @Size(max = 40)
  private String bedNumber;

  @NotBlank
  @Size(max = 80)
  private String ward;

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) {
    this.patientId = patientId;
  }

  public Long getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Long doctorId) {
    this.doctorId = doctorId;
  }

  public String getBedNumber() {
    return bedNumber;
  }

  public void setBedNumber(String bedNumber) {
    this.bedNumber = bedNumber;
  }

  public String getWard() {
    return ward;
  }

  public void setWard(String ward) {
    this.ward = ward;
  }
}
