package com.omya.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.omya.hospital.dto.BillRequest;
import com.omya.hospital.entity.Medicine;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.MedicineRepository;
import com.omya.hospital.repository.PatientRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
  properties = {
    "app.bootstrap.admin.enabled=false",
    "spring.cache.type=simple",
    "management.health.redis.enabled=false",
  }
)
@Testcontainers(disabledWithoutDocker = true)
class BillingConcurrencyIT {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add(
      "app.security.jwt.secret",
      () -> "integration-test-secret-with-at-least-32-bytes"
    );
  }

  @Autowired
  BillingService billingService;

  @Autowired
  BillRepository billRepository;

  @Autowired
  MedicineRepository medicineRepository;

  @Autowired
  PatientRepository patientRepository;

  private Long patientId;

  @BeforeEach
  void seed() {
    billRepository.deleteAll();
    medicineRepository.deleteAll();
    patientRepository.deleteAll();

    Patient patient = new Patient();
    patient.setUhid("UHID-TEST0001");
    patient.setPatientName("Concurrency Test");
    patient.setAge(30);
    patient.setGender("Other");
    patient.setMobile("9000000001");
    patientId = patientRepository.save(patient).getId();

    Medicine medicine = new Medicine();
    medicine.setMedicineName("Single Stock");
    medicine.setBatchNo("BATCH-1");
    medicine.setBarcode("LOCK-1");
    medicine.setMrp(new BigDecimal("20.00"));
    medicine.setPurchasePrice(new BigDecimal("8.00"));
    medicine.setSellingPrice(new BigDecimal("12.00"));
    medicine.setStock(1);
    medicine.setMinimumStock(1);
    medicine.setExpiryDate(LocalDate.now().plusYears(1));
    medicine.setExpiredLocked(false);
    medicine.setLowStock(true);
    medicineRepository.save(medicine);
  }

  @Test
  void pessimisticLockPreventsOverselling() throws Exception {
    try (var executor = Executors.newFixedThreadPool(2)) {
      CountDownLatch start = new CountDownLatch(1);
      Future<String> first = executor.submit(() -> bill(start, "concurrent-1"));
      Future<String> second = executor.submit(() ->
        bill(start, "concurrent-2")
      );
      start.countDown();
      List<String> outcomes = List.of(
        first.get(10, TimeUnit.SECONDS),
        second.get(10, TimeUnit.SECONDS)
      );

      assertThat(outcomes).containsExactlyInAnyOrder(
        "SUCCESS",
        "NOT_ENOUGH_STOCK"
      );
      assertThat(billRepository.count()).isEqualTo(1);
      assertThat(
        medicineRepository.findByBarcode("LOCK-1").orElseThrow().getStock()
      ).isZero();
    }
  }

  @Test
  void repeatedIdempotencyKeyDoesNotChargeTwice() {
    BillRequest request = request("repeat-key");
    var first = billingService.createBill(request);
    var second = billingService.createBill(request);

    assertThat(second.getId()).isEqualTo(first.getId());
    assertThat(billRepository.count()).isEqualTo(1);
    assertThat(
      medicineRepository.findByBarcode("LOCK-1").orElseThrow().getStock()
    ).isZero();
  }

  private String bill(CountDownLatch start, String key)
    throws InterruptedException {
    start.await();
    try {
      billingService.createBill(request(key));
      return "SUCCESS";
    } catch (BusinessRuleException exception) {
      return exception.getMessage();
    }
  }

  private BillRequest request(String key) {
    BillRequest request = new BillRequest();
    request.setPatientId(patientId);
    request.setBarcode("LOCK-1");
    request.setQuantity(1);
    request.setIdempotencyKey(key);
    return request;
  }
}
