package com.omya.hospital.controller;

import com.omya.hospital.dto.SubscriptionRequest;
import com.omya.hospital.entity.Subscription;
import com.omya.hospital.repository.SubscriptionRepository;
import com.omya.hospital.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

  private final SubscriptionService service;

  private final SubscriptionRepository repo;

  public SubscriptionController(
    SubscriptionService service,
    SubscriptionRepository repo
  ) {
    this.service = service;

    this.repo = repo;
  }

  @PostMapping
  public Subscription create(@Valid @RequestBody SubscriptionRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<Subscription> getAll() {
    return repo.findAll();
  }

  @GetMapping("/patient/{patientId}")
  public Subscription patient(@PathVariable Long patientId) {
    return repo

      .findByPatientIdAndStatus(
        patientId,

        "ACTIVE"
      )

      .orElse(null);
  }
}
