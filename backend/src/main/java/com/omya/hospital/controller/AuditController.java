package com.omya.hospital.controller;

import com.omya.hospital.entity.AuditLog;
import com.omya.hospital.repository.AuditLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit")
public class AuditController {

  private final AuditLogRepository repo;

  public AuditController(AuditLogRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<AuditLog> getAll() {
    return repo.findAll();
  }
}
