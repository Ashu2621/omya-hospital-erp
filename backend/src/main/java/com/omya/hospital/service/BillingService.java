package com.omya.hospital.service;

import com.omya.hospital.dto.BillRequest;
import com.omya.hospital.entity.Bill;
import com.omya.hospital.entity.Medicine;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.MedicineRepository;
import com.omya.hospital.repository.PatientRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

  private final BillRepository billRepository;
  private final MedicineRepository medicineRepository;
  private final PatientRepository patientRepository;
  private final AuditService auditService;
  private final JdbcTemplate jdbcTemplate;

  public BillingService(
    BillRepository billRepository,
    MedicineRepository medicineRepository,
    PatientRepository patientRepository,
    AuditService auditService,
    JdbcTemplate jdbcTemplate
  ) {
    this.billRepository = billRepository;
    this.medicineRepository = medicineRepository;
    this.patientRepository = patientRepository;
    this.auditService = auditService;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional
  public Bill createBill(BillRequest request) {
    long idempotencyLock = Integer.toUnsignedLong(
      request.getIdempotencyKey().hashCode()
    );
    jdbcTemplate.execute(
      "SELECT pg_advisory_xact_lock(" + idempotencyLock + ")"
    );
    var previous = billRepository.findByIdempotencyKey(
      request.getIdempotencyKey()
    );
    if (previous.isPresent()) {
      return previous.get();
    }
    if (!patientRepository.existsById(request.getPatientId())) {
      throw new BusinessRuleException("PATIENT_NOT_FOUND");
    }
    Medicine medicine = medicineRepository
      .findWithLockByBarcode(request.getBarcode())
      .orElseThrow(() -> new BusinessRuleException("MEDICINE_NOT_FOUND"));
    if (
      Boolean.TRUE.equals(medicine.getExpiredLocked()) ||
      medicine.getExpiryDate().isBefore(java.time.LocalDate.now())
    ) {
      throw new BusinessRuleException("MEDICINE_EXPIRED");
    }
    if (
      medicine.getStock() == null || medicine.getStock() < request.getQuantity()
    ) {
      throw new BusinessRuleException("NOT_ENOUGH_STOCK");
    }

    medicine.setStock(medicine.getStock() - request.getQuantity());
    medicine.setLowStock(medicine.getStock() <= medicine.getMinimumStock());
    medicineRepository.save(medicine);

    Bill bill = new Bill();
    bill.setPatientId(request.getPatientId());
    bill.setAppointmentId(request.getAppointmentId());
    bill.setMedicineBarcode(medicine.getBarcode());
    bill.setQuantity(request.getQuantity());
    bill.setTotalAmount(
      medicine
        .getSellingPrice()
        .multiply(BigDecimal.valueOf(request.getQuantity()))
    );
    bill.setBillTime(LocalDateTime.now());
    bill.setIdempotencyKey(request.getIdempotencyKey());
    Bill saved = billRepository.save(bill);

    auditService.log(
      "CREATE",
      "BILLING",
      "patient=" +
        request.getPatientId() +
        " medicine=" +
        medicine.getBarcode() +
        " quantity=" +
        request.getQuantity() +
        " amount=" +
        saved.getTotalAmount()
    );
    return saved;
  }

  @Transactional(readOnly = true)
  public List<Bill> getAll() {
    return billRepository.findAll();
  }
}
