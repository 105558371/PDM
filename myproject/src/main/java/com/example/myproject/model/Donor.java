package com.example.myproject.model;

import java.time.LocalDateTime;

public class Donor extends Person {
  private String bloodType;
  private LocalDateTime lastDonate;

  @Override
  public String getRoleLabel() {
    return "Donor";
  }

  public String getBloodType() {
    return bloodType;
  }

  public void setBloodType(String bloodType) {
    this.bloodType = bloodType;
  }

  public LocalDateTime getLastDonate() {
    return lastDonate;
  }

  public void setLastDonate(LocalDateTime lastDonate) {
    this.lastDonate = lastDonate;
  }
}