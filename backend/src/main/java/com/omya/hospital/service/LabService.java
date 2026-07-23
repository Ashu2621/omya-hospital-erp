package com.omya.hospital.service;

import com.omya.hospital.dto.LabRequest;
import com.omya.hospital.entity.LabTest;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.LabRepository;
import com.omya.hospital.repository.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabService {

  private final LabRepository labRepository;
  private final PatientRepository patientRepository;
  private final AppointmentRepository appointmentRepository;

  public LabService(
    LabRepository labRepository,
    PatientRepository patientRepository,
    AppointmentRepository appointmentRepository
  ) {
    this.labRepository = labRepository;
    this.patientRepository = patientRepository;
    this.appointmentRepository = appointmentRepository;
  }

  @Transactional
  public LabTest create(LabRequest request) {
    patientRepository
      .findById(request.getPatientId())
      .orElseThrow(() -> new BusinessRuleException("PATIENT_NOT_FOUND"));

    if (request.getAppointmentId() != null) {
      appointmentRepository
        .findById(request.getAppointmentId())
        .orElseThrow(() -> new BusinessRuleException("APPOINTMENT_NOT_FOUND"));
      if (
        labRepository.existsByAppointmentIdAndTestName(
          request.getAppointmentId(),
          request.getTestName()
        )
      ) {
        throw new BusinessRuleException("TEST_ALREADY_ORDERED");
      }
    }

    LabTest test = new LabTest();
    test.setPatientId(request.getPatientId());
    test.setAppointmentId(request.getAppointmentId());
    test.setTestName(request.getTestName());
    test.setStatus("ORDERED");
    return labRepository.save(test);
  }

  @Transactional
  public LabTest updateStatus(Long id, String status) {
    LabTest test = find(id);
    test.setStatus(status.toUpperCase());
    return labRepository.save(test);
  }

  @Transactional
  public LabTest updateResult(Long id, String result) {
    LabTest test = find(id);
    test.setResult(result);
    test.setReportDate(LocalDate.now());
    test.setStatus("COMPLETED");
    return labRepository.save(test);
  }

  @Transactional(readOnly = true)
  public List<LabTest> getAll() {
    return labRepository.findAll();
  }

  private LabTest find(Long id) {
    return labRepository
      .findById(id)
      .orElseThrow(() -> new BusinessRuleException("LAB_TEST_NOT_FOUND"));
  }
}
