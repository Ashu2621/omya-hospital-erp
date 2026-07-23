package com.omya.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.omya.hospital.dto.BillRequest;
import com.omya.hospital.entity.Bill;
import com.omya.hospital.entity.Medicine;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.MedicineRepository;
import com.omya.hospital.repository.PatientRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

class BillingServiceTest {

  private BillRepository bills;
  private MedicineRepository medicines;
  private PatientRepository patients;
  private AuditService audit;
  private BillingService service;

  @BeforeEach
  void setUp() {
    bills = mock(BillRepository.class);
    medicines = mock(MedicineRepository.class);
    patients = mock(PatientRepository.class);
    audit = mock(AuditService.class);
    service = new BillingService(
      bills,
      medicines,
      patients,
      audit,
      mock(JdbcTemplate.class)
    );
  }

  @Test
  void returnsExistingBillForRepeatedIdempotencyKey() {
    Bill existing = new Bill();
    existing.setId(9L);
    when(bills.findByIdempotencyKey("same-key")).thenReturn(
      Optional.of(existing)
    );

    assertThat(service.createBill(request(2, "same-key"))).isSameAs(existing);
    verify(medicines, never()).findWithLockByBarcode(any());
  }

  @Test
  void rejectsMissingPatient() {
    when(bills.findByIdempotencyKey("key")).thenReturn(Optional.empty());
    when(patients.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> service.createBill(request(1, "key")))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("PATIENT_NOT_FOUND");
  }

  @Test
  void rejectsUnknownMedicine() {
    allowPatient();
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.createBill(request(1, "key")))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("MEDICINE_NOT_FOUND");
  }

  @Test
  void rejectsExpiredMedicine() {
    allowPatient();
    Medicine medicine = medicine(10, "15.50");
    medicine.setExpiredLocked(true);
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(
      Optional.of(medicine)
    );

    assertThatThrownBy(() -> service.createBill(request(1, "key")))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("MEDICINE_EXPIRED");
  }

  @Test
  void rejectsInsufficientStock() {
    allowPatient();
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(
      Optional.of(medicine(1, "15.50"))
    );

    assertThatThrownBy(() -> service.createBill(request(2, "key")))
      .isInstanceOf(BusinessRuleException.class)
      .hasMessage("NOT_ENOUGH_STOCK");
  }

  @Test
  void calculatesMoneyUsingBigDecimalAndReducesStock() {
    allowPatient();
    Medicine medicine = medicine(10, "15.50");
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(
      Optional.of(medicine)
    );
    when(bills.save(any(Bill.class))).thenAnswer(invocation ->
      invocation.getArgument(0)
    );

    Bill saved = service.createBill(request(3, "invoice-1"));

    assertThat(saved.getTotalAmount()).isEqualByComparingTo("46.50");
    assertThat(saved.getQuantity()).isEqualTo(3);
    assertThat(saved.getIdempotencyKey()).isEqualTo("invoice-1");
    assertThat(medicine.getStock()).isEqualTo(7);
    verify(audit).log(any(), any(), any());
  }

  @Test
  void marksMedicineLowStockAfterSale() {
    allowPatient();
    Medicine medicine = medicine(6, "20.00");
    medicine.setMinimumStock(5);
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(
      Optional.of(medicine)
    );
    when(bills.save(any(Bill.class))).thenAnswer(invocation ->
      invocation.getArgument(0)
    );

    service.createBill(request(2, "invoice-2"));

    assertThat(medicine.getLowStock()).isTrue();
  }

  @Test
  void storesExpectedBillReferences() {
    allowPatient();
    Medicine medicine = medicine(8, "10.00");
    when(medicines.findWithLockByBarcode("MED-1")).thenReturn(
      Optional.of(medicine)
    );
    when(bills.save(any(Bill.class))).thenAnswer(invocation ->
      invocation.getArgument(0)
    );

    service.createBill(request(1, "invoice-3"));

    ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
    verify(bills).save(captor.capture());
    assertThat(captor.getValue().getPatientId()).isEqualTo(1L);
    assertThat(captor.getValue().getAppointmentId()).isEqualTo(7L);
    assertThat(captor.getValue().getMedicineBarcode()).isEqualTo("MED-1");
  }

  private void allowPatient() {
    when(bills.findByIdempotencyKey(any())).thenReturn(Optional.empty());
    when(patients.existsById(1L)).thenReturn(true);
  }

  private BillRequest request(int quantity, String key) {
    BillRequest request = new BillRequest();
    request.setPatientId(1L);
    request.setAppointmentId(7L);
    request.setBarcode("MED-1");
    request.setQuantity(quantity);
    request.setIdempotencyKey(key);
    return request;
  }

  private Medicine medicine(int stock, String price) {
    Medicine medicine = new Medicine();
    medicine.setBarcode("MED-1");
    medicine.setMedicineName("Paracetamol");
    medicine.setSellingPrice(new BigDecimal(price));
    medicine.setStock(stock);
    medicine.setMinimumStock(3);
    medicine.setExpiryDate(LocalDate.now().plusYears(1));
    medicine.setExpiredLocked(false);
    medicine.setLowStock(false);
    return medicine;
  }
}
