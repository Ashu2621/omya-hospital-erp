package com.omya.hospital.controller;

import com.omya.hospital.dto.LabRequest;
import com.omya.hospital.entity.LabTest;
import com.omya.hospital.service.LabService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/lab")
public class LabController {

  private final LabService service;

  public LabController(LabService service) {
    this.service = service;
  }

  @PostMapping
  public LabTest create(@Valid @RequestBody LabRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}/status")
  public LabTest updateStatus(
    @PathVariable Long id,
    @RequestParam @Pattern(
      regexp = "ORDERED|COLLECTED|PROCESSING|COMPLETED|CANCELLED"
    ) String status
  ) {
    return service.updateStatus(id, status);
  }

  @PutMapping("/{id}/result")
  public LabTest updateResult(
    @PathVariable Long id,
    @RequestParam @Size(min = 1, max = 1000) String result
  ) {
    return service.updateResult(id, result);
  }

  @GetMapping
  public List<LabTest> getAll() {
    return service.getAll();
  }
}
