package com.omya.hospital.repository;

import com.omya.hospital.entity.Bill;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
  @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b")
  BigDecimal totalRevenue();

  List<Bill> findByPatientId(Long patientId);

  Optional<Bill> findByIdempotencyKey(String idempotencyKey);
}
