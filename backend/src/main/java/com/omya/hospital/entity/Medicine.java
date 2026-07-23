package com.omya.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicines")
public class Medicine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String medicineName;
  private String batchNo;

  @Column(nullable = false, unique = true, length = 100)
  private String barcode;

  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal mrp;

  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal purchasePrice;

  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal sellingPrice;

  private Integer stock;
  private Integer minimumStock;
  private String manufacturer;
  private String rackLocation;
  private LocalDate expiryDate;
  private Boolean expiredLocked = false;
  private Boolean lowStock = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Boolean getExpiredLocked() {
    return expiredLocked;
  }

  public void setExpiredLocked(Boolean expiredLocked) {
    this.expiredLocked = expiredLocked;
  }

  public Boolean getLowStock() {
    return lowStock;
  }

  public void setLowStock(Boolean lowStock) {
    this.lowStock = lowStock;
  }
}
