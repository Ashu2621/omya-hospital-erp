package com.omya.hospital.controller;

import com.omya.hospital.dto.DashboardResponse;
import com.omya.hospital.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  private final DashboardService service;

  public DashboardController(DashboardService service) {
    this.service = service;
  }

  @GetMapping("/stats")
  public DashboardResponse stats() {
    return service.stats();
  }

  @GetMapping("/health")
  public String health() {
    return "DASHBOARD OK";
  }
}
