package com.omya.hospital.controller;

import com.omya.hospital.dto.BillRequest;
import com.omya.hospital.entity.Bill;
import com.omya.hospital.service.BillingService;
import com.omya.hospital.service.PdfService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

  private final BillingService billingService;

  private final PdfService pdfService;

  public BillingController(
    BillingService billingService,
    PdfService pdfService
  ) {
    this.billingService = billingService;

    this.pdfService = pdfService;
  }

  @PostMapping
  public Bill create(@Valid @RequestBody BillRequest request) {
    return billingService.createBill(request);
  }

  @GetMapping
  public List<Bill> getAll() {
    return billingService.getAll();
  }

  @GetMapping("/{id}/pdf")
  public ResponseEntity<byte[]> pdf(@PathVariable Long id) throws Exception {
    byte[] pdf = pdfService.billPdf(id);

    return ResponseEntity.ok()

      .header(
        HttpHeaders.CONTENT_DISPOSITION,

        "attachment; filename=bill.pdf"
      )

      .contentType(MediaType.APPLICATION_PDF)

      .body(pdf);
  }
}
