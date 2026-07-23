package com.omya.hospital.controller;

import com.omya.hospital.dto.MedicineRequest;
import com.omya.hospital.entity.Medicine;
import com.omya.hospital.service.MedicineService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

  private final MedicineService service;

  public MedicineController(MedicineService service) {
    this.service = service;
  }

  @PostMapping
  public Medicine addMedicine(@Valid @RequestBody MedicineRequest request) {
    return service.addMedicine(request);
  }

  @GetMapping
  public List<Medicine> allMedicine() {
    return service.getAll();
  }

  @GetMapping("/barcode/{barcode}")
  public Medicine barcode(@PathVariable String barcode) {
    return service.getBarcode(barcode);
  }
}
