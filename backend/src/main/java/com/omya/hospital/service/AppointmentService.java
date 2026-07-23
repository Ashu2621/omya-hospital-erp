package com.omya.hospital.service;

import com.omya.hospital.dto.AppointmentRequest;
import com.omya.hospital.entity.Appointment;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.DoctorRepository;
import com.omya.hospital.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final AuditService auditService;

  public AppointmentService(
    AppointmentRepository appointmentRepository,
    PatientRepository patientRepository,
    DoctorRepository doctorRepository,
    AuditService auditService
  ) {
    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.auditService = auditService;
  }

  @Transactional
  public Appointment create(AppointmentRequest request) {
    patientRepository
      .findById(request.getPatientId())
      .orElseThrow(() -> new BusinessRuleException("PATIENT_NOT_FOUND"));
    doctorRepository
      .findById(request.getDoctorId())
      .orElseThrow(() -> new BusinessRuleException("DOCTOR_NOT_FOUND"));
    if (
      request.getAppointmentDate().equals(LocalDate.now()) &&
      request.getAppointmentTime().isBefore(LocalTime.now())
    ) {
      throw new BusinessRuleException("APPOINTMENT_TIME_IS_IN_THE_PAST");
    }
    if (
      appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
        request.getDoctorId(),
        request.getAppointmentDate(),
        request.getAppointmentTime()
      )
    ) {
      throw new BusinessRuleException("TIME_SLOT_ALREADY_BOOKED");
    }

    Appointment appointment = new Appointment();
    appointment.setPatientId(request.getPatientId());
    appointment.setDoctorId(request.getDoctorId());
    appointment.setAppointmentDate(request.getAppointmentDate());
    appointment.setAppointmentTime(request.getAppointmentTime());
    appointment.setStatus("BOOKED");
    Appointment saved = appointmentRepository.save(appointment);
    auditService.log(
      "BOOK",
      "APPOINTMENT",
      "patient=" +
        saved.getPatientId() +
        " doctor=" +
        saved.getDoctorId() +
        " date=" +
        saved.getAppointmentDate() +
        " time=" +
        saved.getAppointmentTime()
    );
    return saved;
  }

  @Transactional(readOnly = true)
  public List<Appointment> getAll() {
    return appointmentRepository.findAll();
  }
}
