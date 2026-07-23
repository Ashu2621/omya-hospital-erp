package com.omya.hospital.controller;

import com.omya.hospital.dto.DoctorRequest;
import com.omya.hospital.entity.Doctor;
import com.omya.hospital.service.DoctorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

  private final DoctorService service;

  public DoctorController(DoctorService service) {
    this.service = service;
  }

  @PostMapping
  public Doctor create(@Valid @RequestBody DoctorRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<Doctor> getAll() {
    return service.getAll();
  }
}
