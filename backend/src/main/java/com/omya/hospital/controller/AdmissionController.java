package com.omya.hospital.controller;

import com.omya.hospital.dto.AdmissionRequest;
import com.omya.hospital.entity.Admission;
import com.omya.hospital.service.AdmissionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admission")
public class AdmissionController {

  private final AdmissionService service;

  public AdmissionController(AdmissionService service) {
    this.service = service;
  }

  @PostMapping
  public Admission admit(@Valid @RequestBody AdmissionRequest request) {
    return service.admit(request);
  }

  @PutMapping("/{id}/discharge")
  public Admission discharge(@PathVariable Long id) {
    return service.discharge(id);
  }

  @GetMapping
  public List<Admission> getAll() {
    return service.getAll();
  }

  @GetMapping("/patient/{patientId}")
  public List<Admission> getPatientAdmissions(@PathVariable Long patientId) {
    return service.getPatientAdmissions(patientId);
  }
}
