package com.omya.hospital.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RequestValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void acceptsValidPatient() {
    PatientRequest request = validPatient();
    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void rejectsPatientAgeAbove120() {
    PatientRequest request = validPatient();
    request.setAge(121);
    assertThat(validator.validate(request)).anyMatch(violation ->
      violation.getPropertyPath().toString().equals("age")
    );
  }

  @Test
  void rejectsInvalidMobile() {
    PatientRequest request = validPatient();
    request.setMobile("123");
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void rejectsBlankPatientName() {
    PatientRequest request = validPatient();
    request.setPatientName(" ");
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void rejectsZeroBillingQuantity() {
    BillRequest request = validBill();
    request.setQuantity(0);
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void requiresBillingIdempotencyKey() {
    BillRequest request = validBill();
    request.setIdempotencyKey("");
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void acceptsValidMedicine() {
    MedicineRequest request = validMedicine();
    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void rejectsPastMedicineExpiry() {
    MedicineRequest request = validMedicine();
    request.setExpiryDate(LocalDate.now().minusDays(1));
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void rejectsNegativeMedicinePrice() {
    MedicineRequest request = validMedicine();
    request.setSellingPrice(new BigDecimal("-1.00"));
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void acceptsFutureAppointment() {
    AppointmentRequest request = new AppointmentRequest();
    request.setPatientId(1L);
    request.setDoctorId(2L);
    request.setAppointmentDate(LocalDate.now().plusDays(1));
    request.setAppointmentTime(LocalTime.of(10, 30));
    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void rejectsPastAppointment() {
    AppointmentRequest request = new AppointmentRequest();
    request.setPatientId(1L);
    request.setDoctorId(2L);
    request.setAppointmentDate(LocalDate.now().minusDays(1));
    request.setAppointmentTime(LocalTime.of(10, 30));
    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void rejectsMalformedDoctorEmail() {
    DoctorRequest request = new DoctorRequest();
    request.setDoctorName("Dr. Test");
    request.setSpecialization("Cardiology");
    request.setMobile("9876543210");
    request.setEmail("not-an-email");
    request.setConsultationFee(new BigDecimal("500.00"));
    assertThat(validator.validate(request)).isNotEmpty();
  }

  private PatientRequest validPatient() {
    PatientRequest request = new PatientRequest();
    request.setPatientName("Aditi Sharma");
    request.setAge(32);
    request.setGender("Female");
    request.setMobile("9876543210");
    request.setAddress("Noida");
    return request;
  }

  private BillRequest validBill() {
    BillRequest request = new BillRequest();
    request.setPatientId(1L);
    request.setAppointmentId(1L);
    request.setBarcode("MED-1");
    request.setQuantity(1);
    request.setIdempotencyKey("invoice-1");
    return request;
  }

  private MedicineRequest validMedicine() {
    MedicineRequest request = new MedicineRequest();
    request.setMedicineName("Paracetamol");
    request.setBatchNo("B-1");
    request.setBarcode("MED-1");
    request.setMrp(new BigDecimal("20.00"));
    request.setPurchasePrice(new BigDecimal("10.00"));
    request.setSellingPrice(new BigDecimal("15.00"));
    request.setStock(100);
    request.setMinimumStock(20);
    request.setExpiryDate(LocalDate.now().plusYears(1));
    return request;
  }
}
