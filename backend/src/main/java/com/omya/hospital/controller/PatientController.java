package com.omya.hospital.controller;

import com.omya.hospital.dto.PatientHistoryResponse;
import com.omya.hospital.dto.PatientRequest;
import com.omya.hospital.entity.Patient;
import com.omya.hospital.service.PatientService;
import com.omya.hospital.service.PdfService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

  private final PatientService patientService;

  private final PdfService pdfService;

  public PatientController(
    PatientService patientService,
    PdfService pdfService
  ) {
    this.patientService = patientService;

    this.pdfService = pdfService;
  }

  @PostMapping
  public Patient create(@Valid @RequestBody PatientRequest request) {
    return patientService.create(request);
  }

  @GetMapping
  public List<Patient> getAll() {
    return patientService.getAll();
  }

  @GetMapping("/{id}/history")
  public PatientHistoryResponse history(@PathVariable Long id) {
    return patientService.getHistory(id);
  }

  @GetMapping("/{id}/history/pdf")
  public ResponseEntity<byte[]> patientPdf(@PathVariable Long id) {
    byte[] pdf = pdfService.patientPdf(patientService.getHistory(id));

    return ResponseEntity.ok()

      .header(
        HttpHeaders.CONTENT_DISPOSITION,

        "attachment; filename=patient-summary.pdf"
      )

      .contentType(MediaType.APPLICATION_PDF)

      .body(pdf);
  }

  @GetMapping("/search")
  public Page<Patient> search(
    @RequestParam(defaultValue = "") String query,
    Pageable pageable
  ) {
    return patientService.search(query, pageable);
  }
}
