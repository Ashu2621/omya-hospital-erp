package com.omya.hospital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicineRequest {

  @NotBlank
  @Size(max = 180)
  private String medicineName;

  @NotBlank
  @Size(max = 80)
  private String batchNo;

  @NotBlank
  @Size(max = 100)
  private String barcode;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal mrp;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal purchasePrice;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal sellingPrice;

  @NotNull
  @PositiveOrZero
  private Integer stock;

  @NotNull
  @PositiveOrZero
  private Integer minimumStock;

  @Size(max = 180)
  private String manufacturer;

  @Size(max = 80)
  private String rackLocation;

  @NotNull
  @Future
  private LocalDate expiryDate;

  public String getMedicineName() {
    return medicineName;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public String getBatchNo() {
    return batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public BigDecimal getMrp() {
    return mrp;
  }

  public void setMrp(BigDecimal mrp) {
    this.mrp = mrp;
  }

  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(BigDecimal purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public BigDecimal getSellingPrice() {
    return sellingPrice;
  }

  public void setSellingPrice(BigDecimal sellingPrice) {
    this.sellingPrice = sellingPrice;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public Integer getMinimumStock() {
    return minimumStock;
  }

  public void setMinimumStock(Integer minimumStock) {
    this.minimumStock = minimumStock;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getRackLocation() {
    return rackLocation;
  }

  public void setRackLocation(String rackLocation) {
    this.rackLocation = rackLocation;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }
}
