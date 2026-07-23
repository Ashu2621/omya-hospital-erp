package com.omya.hospital.service;

import com.omya.hospital.dto.PatientHistoryResponse;
import com.omya.hospital.dto.PatientRequest;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AdmissionRepository;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.LabRepository;
import com.omya.hospital.repository.PatientRepository;
import com.omya.hospital.repository.PrescriptionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

  private final PatientRepository patientRepository;
  private final AuditService auditService;
  private final AppointmentRepository appointmentRepository;
  private final PrescriptionRepository prescriptionRepository;
  private final LabRepository labRepository;
  private final AdmissionRepository admissionRepository;
  private final BillRepository billRepository;

  public PatientService(
    PatientRepository patientRepository,
    AuditService auditService,
    AppointmentRepository appointmentRepository,
    PrescriptionRepository prescriptionRepository,
    LabRepository labRepository,
    AdmissionRepository admissionRepository,
    BillRepository billRepository
  ) {
    this.patientRepository = patientRepository;
    this.auditService = auditService;
    this.appointmentRepository = appointmentRepository;
    this.prescriptionRepository = prescriptionRepository;
    this.labRepository = labRepository;
    this.admissionRepository = admissionRepository;
    this.billRepository = billRepository;
  }

  @Transactional
  public Patient create(PatientRequest request) {
    if (patientRepository.existsByMobile(request.getMobile())) {
      throw new BusinessRuleException("PATIENT_ALREADY_EXISTS");
    }
    Patient patient = new Patient();
    patient.setUhid(
      "UHID-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
    );
    patient.setPatientName(request.getPatientName());
    patient.setAge(request.getAge());
    patient.setGender(request.getGender());
    patient.setMobile(request.getMobile());
    patient.setAddress(request.getAddress());
    Patient saved = patientRepository.save(patient);
    auditService.log("CREATE", "PATIENT", "patient=" + saved.getUhid());
    return saved;
  }

  @Transactional(readOnly = true)
  public List<Patient> getAll() {
    return patientRepository.findAll();
  }

  @Transactional(readOnly = true)
  public PatientHistoryResponse getHistory(Long patientId) {
    Patient patient = patientRepository
      .findById(patientId)
      .orElseThrow(() -> new BusinessRuleException("PATIENT_NOT_FOUND"));
    var appointments = appointmentRepository.findByPatientId(patientId);
    var appointmentIds = appointments
      .stream()
      .map(appointment -> appointment.getId())
      .toList();

    PatientHistoryResponse response = new PatientHistoryResponse();
    response.setPatient(patient);
    response.setAppointments(appointments);
    response.setPrescriptions(
      appointmentIds.isEmpty()
        ? List.of()
        : prescriptionRepository.findByAppointmentIdIn(appointmentIds)
    );
    response.setLabTests(labRepository.findByPatientId(patientId));
    response.setAdmissions(admissionRepository.findByPatientId(patientId));
    response.setBills(billRepository.findByPatientId(patientId));
    return response;
  }

  @Transactional(readOnly = true)
  public Page<Patient> search(String query, Pageable pageable) {
    return patientRepository.findByPatientNameContainingIgnoreCaseOrUhidContainingIgnoreCase(
      query,
      query,
      pageable
    );
  }
}
