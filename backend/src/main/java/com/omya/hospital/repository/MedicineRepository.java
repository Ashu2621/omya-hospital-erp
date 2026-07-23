package com.omya.hospital.repository;

import com.omya.hospital.entity.Medicine;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
  Optional<Medicine> findByBarcode(String barcode);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
    "SELECT medicine FROM Medicine medicine WHERE medicine.barcode = :barcode"
  )
  Optional<Medicine> findWithLockByBarcode(@Param("barcode") String barcode);

  boolean existsByBarcode(String barcode);

  long countByLowStockTrue();

  long countByExpiredLockedTrue();

  List<Medicine> findByMedicineNameContainingIgnoreCase(String medicineName);

  List<Medicine> findByManufacturerContainingIgnoreCase(String manufacturer);

  List<Medicine> findByExpiryDateBefore(LocalDate expiryDate);

  List<Medicine> findByExpiryDateBetween(LocalDate start, LocalDate end);

  List<Medicine> findByLowStockTrue();

  List<Medicine> findByExpiredLockedTrue();

  List<Medicine> findByStockLessThanEqual(Integer stock);

  long countByStockGreaterThan(Integer stock);
}
