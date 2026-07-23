package com.omya.hospital.scheduler;

import com.omya.hospital.entity.Subscription;
import com.omya.hospital.repository.SubscriptionRepository;
import com.omya.hospital.service.AuditService;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SubscriptionScheduler {

  private static final long RENEWAL_LOCK_ID = 6_114_203_981L;

  private final SubscriptionRepository subscriptionRepository;
  private final AuditService auditService;
  private final JdbcTemplate jdbcTemplate;

  public SubscriptionScheduler(
    SubscriptionRepository subscriptionRepository,
    AuditService auditService,
    JdbcTemplate jdbcTemplate
  ) {
    this.subscriptionRepository = subscriptionRepository;
    this.auditService = auditService;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Scheduled(cron = "${app.subscriptions.renewal-cron}")
  @Transactional
  public void renewSubscriptions() {
    Boolean acquired = jdbcTemplate.queryForObject(
      "SELECT pg_try_advisory_xact_lock(?)",
      Boolean.class,
      RENEWAL_LOCK_ID
    );
    if (!Boolean.TRUE.equals(acquired)) {
      return;
    }

    subscriptionRepository
      .findByEndDateBeforeAndStatus(LocalDate.now(), "ACTIVE")
      .forEach(this::renewOrExpire);
  }

  private void renewOrExpire(Subscription subscription) {
    if (!Boolean.TRUE.equals(subscription.getAutoRenew())) {
      subscription.setStatus("EXPIRED");
      subscriptionRepository.save(subscription);
      auditService.log(
        "EXPIRE",
        "SUBSCRIPTION",
        "patient=" + subscription.getPatientId()
      );
      return;
    }

    LocalDate today = LocalDate.now();
    subscription.setStartDate(today);
    if ("MONTHLY".equalsIgnoreCase(subscription.getPlanType())) {
      subscription.setEndDate(today.plusMonths(1));
    } else if ("YEARLY".equalsIgnoreCase(subscription.getPlanType())) {
      subscription.setEndDate(today.plusYears(1));
    } else {
      subscription.setStatus("EXPIRED");
    }
    subscriptionRepository.save(subscription);
    auditService.log(
      "AUTO_RENEW",
      "SUBSCRIPTION",
      "patient=" +
        subscription.getPatientId() +
        " end=" +
        subscription.getEndDate()
    );
  }
}
