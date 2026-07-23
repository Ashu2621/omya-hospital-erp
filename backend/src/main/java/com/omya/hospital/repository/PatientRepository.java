package com.omya.hospital.repository;

import com.omya.hospital.entity.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
  Optional<Patient> findByUhid(String uhid);
  Optional<Patient> findByMobile(String mobile);
  boolean existsByMobile(String mobile);
  List<Patient> findByPatientNameContainingIgnoreCase(String patientName);
  Page<Patient> findByPatientNameContainingIgnoreCaseOrUhidContainingIgnoreCase(
    String patientName,
    String uhid,
    Pageable pageable
  );
}
