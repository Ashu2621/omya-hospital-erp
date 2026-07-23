package com.omya.hospital.service;

import com.omya.hospital.dto.PrescriptionRequest;
import com.omya.hospital.entity.Prescription;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.MedicineRepository;
import com.omya.hospital.repository.PrescriptionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionService {

  private final PrescriptionRepository prescriptionRepository;
  private final AppointmentRepository appointmentRepository;
  private final MedicineRepository medicineRepository;

  public PrescriptionService(
    PrescriptionRepository prescriptionRepository,
    AppointmentRepository appointmentRepository,
    MedicineRepository medicineRepository
  ) {
    this.prescriptionRepository = prescriptionRepository;
    this.appointmentRepository = appointmentRepository;
    this.medicineRepository = medicineRepository;
  }

  @Transactional
  public Prescription create(PrescriptionRequest request) {
    appointmentRepository
      .findById(request.getAppointmentId())
      .orElseThrow(() -> new BusinessRuleException("APPOINTMENT_NOT_FOUND"));
    medicineRepository
      .findByBarcode(request.getMedicineBarcode())
      .orElseThrow(() -> new BusinessRuleException("MEDICINE_NOT_FOUND"));
    if (
      prescriptionRepository.existsByAppointmentIdAndMedicineBarcode(
        request.getAppointmentId(),
        request.getMedicineBarcode()
      )
    ) {
      throw new BusinessRuleException("MEDICINE_ALREADY_PRESCRIBED");
    }
    Prescription prescription = new Prescription();
    prescription.setAppointmentId(request.getAppointmentId());
    prescription.setMedicineBarcode(request.getMedicineBarcode());
    prescription.setDosage(request.getDosage());
    prescription.setDays(request.getDays());
    return prescriptionRepository.save(prescription);
  }

  @Transactional(readOnly = true)
  public List<Prescription> getAll() {
    return prescriptionRepository.findAll();
  }
}
