package com.example.myproject.model;

public class Donor extends Person {
  private String bloodType;
  private String lastDonate;

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

  public String getLastDonate() {
    return lastDonate;
  }

  public void setLastDonate(String lastDonate) {
    this.lastDonate = lastDonate;
  }
}