package com.omya.hospital.repository;

import com.omya.hospital.entity.Doctor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
  Optional<Doctor> findByMobile(String mobile);
}
