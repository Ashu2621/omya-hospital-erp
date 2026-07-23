package com.omya.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "admissions")
public class Admission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long patientId;

  private Long doctorId;

  private String bedNumber;

  private String ward;

  private LocalDate admissionDate;

  private LocalDate dischargeDate;

  private String status;

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

  public LocalDate getAdmissionDate() {
    return admissionDate;
  }

  public void setAdmissionDate(LocalDate admissionDate) {
    this.admissionDate = admissionDate;
  }

  public LocalDate getDischargeDate() {
    return dischargeDate;
  }

  public void setDischargeDate(LocalDate dischargeDate) {
    this.dischargeDate = dischargeDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
