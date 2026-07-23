package com.omya.hospital.service;

import com.omya.hospital.dto.DashboardResponse;
import com.omya.hospital.repository.AdmissionRepository;
import com.omya.hospital.repository.AppointmentRepository;
import com.omya.hospital.repository.BillRepository;
import com.omya.hospital.repository.DoctorRepository;
import com.omya.hospital.repository.LabRepository;
import com.omya.hospital.repository.MedicineRepository;
import com.omya.hospital.repository.PatientRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

  private static final long TOTAL_BEDS = 360L;

  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final AppointmentRepository appointmentRepository;
  private final AdmissionRepository admissionRepository;
  private final MedicineRepository medicineRepository;
  private final LabRepository labRepository;
  private final BillRepository billRepository;

  public DashboardService(
    PatientRepository patientRepository,
    DoctorRepository doctorRepository,
    AppointmentRepository appointmentRepository,
    AdmissionRepository admissionRepository,
    MedicineRepository medicineRepository,
    LabRepository labRepository,
    BillRepository billRepository
  ) {
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.appointmentRepository = appointmentRepository;
    this.admissionRepository = admissionRepository;
    this.medicineRepository = medicineRepository;
    this.labRepository = labRepository;
    this.billRepository = billRepository;
  }

  @Cacheable("dashboard-stats")
  @Transactional(readOnly = true)
  public DashboardResponse stats() {
    DashboardResponse response = new DashboardResponse();
    long occupiedBeds = admissionRepository.countByStatus("ADMITTED");
    BigDecimal revenue = billRepository.totalRevenue();
    response.setPatients(patientRepository.count());
    response.setDoctors(doctorRepository.count());
    response.setAppointments(appointmentRepository.count());
    response.setTodayAppointments(
      appointmentRepository.countByAppointmentDate(LocalDate.now())
    );
    response.setAdmittedPatients(occupiedBeds);
    response.setOccupiedBeds(occupiedBeds);
    response.setAvailableBeds(Math.max(0, TOTAL_BEDS - occupiedBeds));
    response.setMedicines(medicineRepository.count());
    response.setLowStockMedicines(medicineRepository.countByLowStockTrue());
    response.setLabTests(labRepository.count());
    response.setRevenue(revenue == null ? BigDecimal.ZERO : revenue);
    return response;
  }
}
