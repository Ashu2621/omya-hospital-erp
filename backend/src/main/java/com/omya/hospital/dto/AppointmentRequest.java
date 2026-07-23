package com.omya.hospital.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

  @NotNull
  @Positive
  private Long patientId;

  @NotNull
  @Positive
  private Long doctorId;

  @NotNull
  @FutureOrPresent
  private LocalDate appointmentDate;

  @NotNull
  private LocalTime appointmentTime;

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

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(LocalDate appointmentDate) {
    this.appointmentDate = appointmentDate;
  }

  public LocalTime getAppointmentTime() {
    return appointmentTime;
  }

  public void setAppointmentTime(LocalTime appointmentTime) {
    this.appointmentTime = appointmentTime;
  }
}
