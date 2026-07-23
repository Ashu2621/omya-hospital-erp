package com.omya.hospital.dto;

import com.omya.hospital.entity.Admission;
import com.omya.hospital.entity.Appointment;
import com.omya.hospital.entity.Bill;
import com.omya.hospital.entity.LabTest;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.entity.Prescription;
import java.util.List;

public class PatientHistoryResponse {

  private Patient patient;

  private List<Appointment> appointments;

  private List<Prescription> prescriptions;

  private List<LabTest> labTests;

  private List<Bill> bills;

  private List<Admission> admissions;

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public List<Appointment> getAppointments() {
    return appointments;
  }

  public void setAppointments(List<Appointment> appointments) {
    this.appointments = appointments;
  }

  public List<Prescription> getPrescriptions() {
    return prescriptions;
  }

  public void setPrescriptions(List<Prescription> prescriptions) {
    this.prescriptions = prescriptions;
  }

  public List<LabTest> getLabTests() {
    return labTests;
  }

  public void setLabTests(List<LabTest> labTests) {
    this.labTests = labTests;
  }

  public List<Bill> getBills() {
    return bills;
  }

  public void setBills(List<Bill> bills) {
    this.bills = bills;
  }

  public List<Admission> getAdmissions() {
    return admissions;
  }

  public void setAdmissions(List<Admission> admissions) {
    this.admissions = admissions;
  }
}
