package com.omya.hospital.controller;

import com.omya.hospital.dto.PrescriptionRequest;
import com.omya.hospital.entity.Prescription;
import com.omya.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

  private final PrescriptionService service;

  public PrescriptionController(PrescriptionService service) {
    this.service = service;
  }

  @PostMapping
  public Prescription create(@Valid @RequestBody PrescriptionRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<Prescription> getAll() {
    return service.getAll();
  }
}
