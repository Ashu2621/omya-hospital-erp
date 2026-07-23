package com.omya.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long patientId;
  private Long appointmentId;
  private String medicineBarcode;
  private Integer quantity;

  @Column(precision = 14, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  private LocalDateTime billTime;

  @Column(nullable = false, unique = true, length = 100)
  private String idempotencyKey;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getMedicineBarcode() {
    return medicineBarcode;
  }

  public void setMedicineBarcode(String medicineBarcode) {
    this.medicineBarcode = medicineBarcode;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public LocalDateTime getBillTime() {
    return billTime;
  }

  public void setBillTime(LocalDateTime billTime) {
    this.billTime = billTime;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }
}
