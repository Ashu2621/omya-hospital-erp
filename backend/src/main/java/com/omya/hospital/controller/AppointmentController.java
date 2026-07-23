package com.omya.hospital.controller;

import com.omya.hospital.dto.AppointmentRequest;
import com.omya.hospital.entity.Appointment;
import com.omya.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

  private final AppointmentService service;

  public AppointmentController(AppointmentService service) {
    this.service = service;
  }

  @PostMapping
  public Appointment create(@Valid @RequestBody AppointmentRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<Appointment> getAll() {
    return service.getAll();
  }
}
