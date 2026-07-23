package com.omya.hospital.dto;

import java.math.BigDecimal;

public class DashboardResponse {

  private Long patients;
  private Long doctors;
  private Long appointments;
  private Long todayAppointments;
  private Long admittedPatients;
  private Long occupiedBeds;
  private Long availableBeds;
  private Long medicines;
  private Long lowStockMedicines;
  private Long labTests;
  private BigDecimal revenue;

  public Long getPatients() {
    return patients;
  }

  public void setPatients(Long patients) {
    this.patients = patients;
  }

  public Long getDoctors() {
    return doctors;
  }

  public void setDoctors(Long doctors) {
    this.doctors = doctors;
  }

  public Long getAppointments() {
    return appointments;
  }

  public void setAppointments(Long appointments) {
    this.appointments = appointments;
  }

  public Long getTodayAppointments() {
    return todayAppointments;
  }

  public void setTodayAppointments(Long todayAppointments) {
    this.todayAppointments = todayAppointments;
  }

  public Long getAdmittedPatients() {
    return admittedPatients;
  }

  public void setAdmittedPatients(Long admittedPatients) {
    this.admittedPatients = admittedPatients;
  }

  public Long getOccupiedBeds() {
    return occupiedBeds;
  }

  public void setOccupiedBeds(Long occupiedBeds) {
    this.occupiedBeds = occupiedBeds;
  }

  public Long getAvailableBeds() {
    return availableBeds;
  }

  public void setAvailableBeds(Long availableBeds) {
    this.availableBeds = availableBeds;
  }

  public Long getMedicines() {
    return medicines;
  }

  public void setMedicines(Long medicines) {
    this.medicines = medicines;
  }

  public Long getLowStockMedicines() {
    return lowStockMedicines;
  }

  public void setLowStockMedicines(Long lowStockMedicines) {
    this.lowStockMedicines = lowStockMedicines;
  }

  public Long getLabTests() {
    return labTests;
  }

  public void setLabTests(Long labTests) {
    this.labTests = labTests;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }
}
