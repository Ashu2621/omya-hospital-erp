package com.omya.hospital.service;

import com.omya.hospital.dto.AdmissionRequest;
import com.omya.hospital.entity.Admission;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AdmissionRepository;
import com.omya.hospital.repository.DoctorRepository;
import com.omya.hospital.repository.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdmissionService {

  private final AdmissionRepository admissionRepository;
  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final AuditService auditService;

  public AdmissionService(
    AdmissionRepository admissionRepository,
    PatientRepository patientRepository,
    DoctorRepository doctorRepository,
    AuditService auditService
  ) {
    this.admissionRepository = admissionRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.auditService = auditService;
  }

  @Transactional
  public Admission admit(AdmissionRequest request) {
    patientRepository
      .findById(request.getPatientId())
      .orElseThrow(() -> new BusinessRuleException("PATIENT_NOT_FOUND"));
    doctorRepository
      .findById(request.getDoctorId())
      .orElseThrow(() -> new BusinessRuleException("DOCTOR_NOT_FOUND"));
    if (
      admissionRepository.existsByBedNumberAndStatus(
        request.getBedNumber(),
        "ADMITTED"
      )
    ) {
      throw new BusinessRuleException("BED_OCCUPIED");
    }

    Admission admission = new Admission();
    admission.setPatientId(request.getPatientId());
    admission.setDoctorId(request.getDoctorId());
    admission.setBedNumber(request.getBedNumber());
    admission.setWard(request.getWard());
    admission.setAdmissionDate(LocalDate.now());
    admission.setStatus("ADMITTED");
    Admission saved = admissionRepository.save(admission);
    auditService.log(
      "ADMIT",
      "ADMISSION",
      "patient=" + saved.getPatientId() + " bed=" + saved.getBedNumber()
    );
    return saved;
  }

  @Transactional
  public Admission discharge(Long id) {
    Admission admission = admissionRepository
      .findById(id)
      .orElseThrow(() -> new BusinessRuleException("ADMISSION_NOT_FOUND"));
    if ("DISCHARGED".equals(admission.getStatus())) {
      throw new BusinessRuleException("PATIENT_ALREADY_DISCHARGED");
    }
    admission.setDischargeDate(LocalDate.now());
    admission.setStatus("DISCHARGED");
    Admission saved = admissionRepository.save(admission);
    auditService.log(
      "DISCHARGE",
      "ADMISSION",
      "patient=" + saved.getPatientId() + " bed=" + saved.getBedNumber()
    );
    return saved;
  }

  @Transactional(readOnly = true)
  public List<Admission> getAll() {
    return admissionRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Admission> getPatientAdmissions(Long patientId) {
    return admissionRepository.findByPatientId(patientId);
  }
}
