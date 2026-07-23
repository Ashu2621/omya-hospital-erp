package com.omya.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BillRequest {

  @NotNull(message = "Patient is required")
  @Positive(message = "Patient id must be positive")
  private Long patientId;

  @Positive(message = "Appointment id must be positive")
  private Long appointmentId;

  @NotBlank(message = "Medicine barcode is required")
  @Size(max = 100, message = "Barcode is too long")
  private String barcode;

  @NotNull(message = "Quantity is required")
  @Positive(message = "Quantity must be positive")
  private Integer quantity;

  @NotBlank(message = "Idempotency key is required")
  @Size(max = 100, message = "Idempotency key is too long")
  private String idempotencyKey;

  public Long getPatientId() {
    return patientId;
  }

  public void setPatientId(Long patientId) {
    this.patientId = patientId;
  }

  public Long getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(Long appointmentId) {
    this.appointmentId = appointmentId;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }
}
