package com.omya.hospital.service;

import com.omya.hospital.dto.DoctorRequest;
import com.omya.hospital.entity.Doctor;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.DoctorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

  private final DoctorRepository doctorRepository;

  public DoctorService(DoctorRepository doctorRepository) {
    this.doctorRepository = doctorRepository;
  }

  @Transactional
  public Doctor create(DoctorRequest request) {
    if (doctorRepository.findByMobile(request.getMobile()).isPresent()) {
      throw new BusinessRuleException("DOCTOR_ALREADY_EXISTS");
    }
    Doctor doctor = new Doctor();
    doctor.setDoctorName(request.getDoctorName());
    doctor.setSpecialization(request.getSpecialization());
    doctor.setMobile(request.getMobile());
    doctor.setEmail(request.getEmail());
    doctor.setConsultationFee(request.getConsultationFee());
    return doctorRepository.save(doctor);
  }

  @Transactional(readOnly = true)
  public List<Doctor> getAll() {
    return doctorRepository.findAll();
  }
}
