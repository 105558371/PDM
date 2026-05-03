package com.example.myproject.model;

import java.time.LocalDateTime;

public class BloodDonation {
  private int donationId;
  private LocalDateTime date;
  private String donateAddr;
  private int donorId;
  private String donorName; // for display

  // getters/setters
  public int getDonationId() {
    return donationId;
  }

  public void setDonationId(int donationId) {
    this.donationId = donationId;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getDonateAddr() {
    return donateAddr;
  }

  public void setDonateAddr(String donateAddr) {
    this.donateAddr = donateAddr;
  }

  public int getDonorId() {
    return donorId;
  }

  public void setDonorId(int donorId) {
    this.donorId = donorId;
  }

  public String getDonorName() {
    return donorName;
  }

  public void setDonorName(String donorName) {
    this.donorName = donorName;
  }
}