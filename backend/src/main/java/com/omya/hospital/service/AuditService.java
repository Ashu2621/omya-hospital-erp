package com.omya.hospital.service;

import com.omya.hospital.entity.AuditLog;
import com.omya.hospital.repository.AuditLogRepository;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

  private final AuditLogRepository repo;

  public AuditService(AuditLogRepository repo) {
    this.repo = repo;
  }

  public void log(String action, String module, String details) {
    Authentication auth =
      SecurityContextHolder.getContext().getAuthentication();

    String user = "SYSTEM";

    if (auth != null) {
      user = auth.getName();
    }

    AuditLog log = new AuditLog();

    log.setUsername(user);

    log.setAction(action);

    log.setModuleName(module);

    log.setDetails(details);

    log.setTimestamp(LocalDateTime.now());

    repo.save(log);
  }
}
