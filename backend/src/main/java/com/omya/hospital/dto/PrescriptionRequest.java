package com.omya.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PrescriptionRequest {

  @NotNull
  @Positive
  private Long appointmentId;

  @NotBlank
  @Size(max = 100)
  private String medicineBarcode;

  @NotBlank
  @Size(max = 120)
  private String dosage;

  @NotNull
  @Positive
  private Integer days;

  public Long getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(Long appointmentId) {
    this.appointmentId = appointmentId;
  }

  public String getMedicineBarcode() {
    return medicineBarcode;
  }

  public void setMedicineBarcode(String medicineBarcode) {
    this.medicineBarcode = medicineBarcode;
  }

  public String getDosage() {
    return dosage;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }
}
