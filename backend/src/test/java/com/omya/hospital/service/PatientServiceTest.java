package com.omya.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.omya.hospital.dto.PatientRequest;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AdmissionRepository;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.LabRepository;
import com.omya.hospital.repository.PatientRepository;
import com.omya.hospital.repository.PrescriptionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientServiceTest {

  private PatientRepository patients;
  private AuditService audit;
  private PatientService service;

  @BeforeEach
  void setUp() {
    patients = mock(PatientRepository.class);
    audit = mock(AuditService.class);
    service = new PatientService(
      patients,
      audit,
      mock(AppointmentRepository.class),
      mock(PrescriptionRepository.class),
      mock(LabRepository.class),
      mock(AdmissionRepository.class),
      mock(BillRepository.class)
    );
  }

  @Test
  void createsPatientAndGeneratesUhid() {
    when(patients.save(any(Patient.class))).thenAnswer(invocation ->
      invocation.getArgument(0)
    );

    Patient created = service.create(validRequest());

    assertThat(created.getUhid()).startsWith("UHID-").hasSize(13);
    assertThat(created.getPatientName()).isEqualTo("Aditi Sharma");
    verify(audit).log(eq("CREATE"), eq("PATIENT"), any(String.class));
  }

  @Test
  void rejectsDuplicateMobile() {
    when(patients.existsByMobile("9876543210")).thenReturn(true);
    assertThatThrownBy(() -> service.create(validRequest()))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("PATIENT_ALREADY_EXISTS");
  }

  @Test
  void copiesValidatedDemographics() {
    when(patients.save(any(Patient.class))).thenAnswer(invocation ->
      invocation.getArgument(0)
    );

    Patient created = service.create(validRequest());

    assertThat(created.getAge()).isEqualTo(32);
    assertThat(created.getGender()).isEqualTo("Female");
    assertThat(created.getAddress()).isEqualTo("Noida");
  }

  @Test
  void rejectsHistoryForUnknownPatient() {
    when(patients.findById(99L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.getHistory(99L))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("PATIENT NOT FOUND");
  }

  private PatientRequest validRequest() {
    PatientRequest request = new PatientRequest();
    request.setPatientName("Aditi Sharma");
    request.setAge(32);
    request.setGender("Female");
    request.setMobile("9876543210");
    request.setAddress("Noida");
    return request;
  }
}
