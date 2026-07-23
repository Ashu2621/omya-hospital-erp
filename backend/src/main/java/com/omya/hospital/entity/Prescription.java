package com.omya.hospital.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prescriptions")
public class Prescription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long appointmentId;

  private String medicineBarcode;

  private String dosage;

  private Integer days;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
