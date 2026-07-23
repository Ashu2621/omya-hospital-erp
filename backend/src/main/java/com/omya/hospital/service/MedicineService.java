package com.omya.hospital.service;

import com.omya.hospital.dto.MedicineRequest;
import com.omya.hospital.entity.Medicine;
import com.omya.hospital.exception.BusinessRuleException;
import com.omya.hospital.repository.MedicineRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicineService {

  private final MedicineRepository medicineRepository;
  private final AuditService auditService;

  public MedicineService(
    MedicineRepository medicineRepository,
    AuditService auditService
  ) {
    this.medicineRepository = medicineRepository;
    this.auditService = auditService;
  }

  @Transactional
  public Medicine addMedicine(MedicineRequest request) {
    if (medicineRepository.existsByBarcode(request.getBarcode())) {
      throw new BusinessRuleException("MEDICINE_BARCODE_EXISTS");
    }

    Medicine medicine = new Medicine();
    medicine.setMedicineName(request.getMedicineName());
    medicine.setBatchNo(request.getBatchNo());
    medicine.setBarcode(request.getBarcode());
    medicine.setMrp(request.getMrp());
    medicine.setPurchasePrice(request.getPurchasePrice());
    medicine.setSellingPrice(request.getSellingPrice());
    medicine.setStock(request.getStock());
    medicine.setMinimumStock(request.getMinimumStock());
    medicine.setManufacturer(request.getManufacturer());
    medicine.setRackLocation(request.getRackLocation());
    medicine.setExpiryDate(request.getExpiryDate());
    medicine.setExpiredLocked(false);
    medicine.setLowStock(request.getStock() <= request.getMinimumStock());

    Medicine saved = medicineRepository.save(medicine);
    auditService.log("CREATE", "MEDICINE", "barcode=" + saved.getBarcode());
    return saved;
  }

  @Transactional(readOnly = true)
  public List<Medicine> getAll() {
    return medicineRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Medicine getBarcode(String barcode) {
    return medicineRepository
      .findByBarcode(barcode)
      .orElseThrow(() -> new BusinessRuleException("MEDICINE_NOT_FOUND"));
  }
}
