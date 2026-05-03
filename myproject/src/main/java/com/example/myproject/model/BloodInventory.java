package com.example.myproject.model;

import java.time.LocalDate;

public class BloodInventory {
  private int inventoryId;
  private String bloodType;
  private LocalDate expdate;
  private String status;
  private int bankId;
  private String bankName;
  private int donationId;

  // getters/setters
  public int getInventoryId() {
    return inventoryId;
  }

  public void setInventoryId(int inventoryId) {
    this.inventoryId = inventoryId;
  }

  public String getBloodType() {
    return bloodType;
  }

  public void setBloodType(String bloodType) {
    this.bloodType = bloodType;
  }

  public LocalDate getExpdate() {
    return expdate;
  }

  public void setExpdate(LocalDate expdate) {
    this.expdate = expdate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getBankId() {
    return bankId;
  }

  public void setBankId(int bankId) {
    this.bankId = bankId;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public int getDonationId() {
    return donationId;
  }

  public void setDonationId(int donationId) {
    this.donationId = donationId;
  }
}