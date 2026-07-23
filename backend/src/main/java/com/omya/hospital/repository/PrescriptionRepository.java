package com.omya.hospital.repository;

import com.omya.hospital.entity.Prescription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository
  extends JpaRepository<Prescription, Long>
{
  boolean existsByAppointmentIdAndMedicineBarcode(
    Long appointmentId,
    String medicineBarcode
  );

  List<Prescription> findByAppointmentIdIn(List<Long> appointmentIds);
}
