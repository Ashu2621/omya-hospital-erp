package com.omya.hospital.repository;

import com.omya.hospital.entity.Admission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
  boolean existsByBedNumberAndStatus(String bedNumber, String status);

  long countByStatus(String status);

  List<Admission> findByPatientId(Long patientId);
}
