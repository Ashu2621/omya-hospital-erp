package com.omya.hospital.repository;

import com.omya.hospital.entity.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository
  extends JpaRepository<Appointment, Long>
{
  boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
    Long doctorId,
    LocalDate appointmentDate,
    LocalTime appointmentTime
  );

  List<Appointment> findByPatientId(Long patientId);

  List<Appointment> findByDoctorId(Long doctorId);

  List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

  long countByAppointmentDate(LocalDate appointmentDate);

  long countByStatus(String status);
}
