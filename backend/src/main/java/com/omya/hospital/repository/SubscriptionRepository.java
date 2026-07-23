package com.omya.hospital.repository;

import com.omya.hospital.entity.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository
  extends JpaRepository<Subscription, Long>
{
  Optional<Subscription> findByPatientIdAndStatus(
    Long patientId,
    String status
  );

  List<Subscription> findByStatus(String status);

  List<Subscription> findByEndDateBeforeAndStatus(
    LocalDate date,
    String status
  );
}
