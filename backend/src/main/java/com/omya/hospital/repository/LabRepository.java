package com.omya.hospital.repository;

import com.omya.hospital.entity.LabTest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabRepository extends JpaRepository<LabTest, Long> {
  boolean existsByAppointmentIdAndTestName(Long appointmentId, String testName);

  List<LabTest> findByPatientId(Long patientId);

  List<LabTest> findByStatus(String status);

  List<LabTest> findByAppointmentId(Long appointmentId);

  long countByStatus(String status);

  long count();
}
