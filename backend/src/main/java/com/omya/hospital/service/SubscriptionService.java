package com.omya.hospital.service;

import com.omya.hospital.dto.SubscriptionRequest;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.entity.Subscription;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.DoctorRepository;
import com.omya.hospital.repository.PatientRepository;
import com.omya.hospital.repository.SubscriptionRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final AuditService auditService;

  public SubscriptionService(
    SubscriptionRepository subscriptionRepository,
    PatientRepository patientRepository,
    DoctorRepository doctorRepository,
    AuditService auditService
  ) {
    this.subscriptionRepository = subscriptionRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.auditService = auditService;
  }

  @Transactional
  public Subscription create(SubscriptionRequest request) {
    Patient patient = patientRepository
      .findById(request.getPatientId())
      .orElseThrow(() -> new BusinessRuleException("PATIENT_NOT_FOUND"));
    if (patient.getAge() > 12) {
      throw new BusinessRuleException(
        "SUBSCRIPTION_ONLY_AVAILABLE_FOR_CHILDREN"
      );
    }
    if (
      request.getDoctorId() != null &&
      !doctorRepository.existsById(request.getDoctorId())
    ) {
      throw new BusinessRuleException("DOCTOR_NOT_FOUND");
    }
    if (
      subscriptionRepository
        .findByPatientIdAndStatus(request.getPatientId(), "ACTIVE")
        .isPresent()
    ) {
      throw new BusinessRuleException("ACTIVE_SUBSCRIPTION_ALREADY_EXISTS");
    }

    LocalDate startDate = LocalDate.now();
    String plan = request.getPlanType().toUpperCase();
    LocalDate endDate = switch (plan) {
      case "MONTHLY" -> startDate.plusMonths(1);
      case "YEARLY" -> startDate.plusYears(1);
      default -> throw new BusinessRuleException("INVALID_PLAN_TYPE");
    };

    Subscription subscription = new Subscription();
    subscription.setPatientId(request.getPatientId());
    subscription.setDoctorId(request.getDoctorId());
    subscription.setPlanType(plan);
    subscription.setAmount(request.getAmount());
    subscription.setStartDate(startDate);
    subscription.setEndDate(endDate);
    subscription.setStatus("ACTIVE");
    subscription.setAutoRenew(Boolean.TRUE.equals(request.getAutoRenew()));
    Subscription saved = subscriptionRepository.save(subscription);
    auditService.log(
      "CREATE",
      "SUBSCRIPTION",
      "patient=" + saved.getPatientId() + " plan=" + saved.getPlanType()
    );
    return saved;
  }
}
