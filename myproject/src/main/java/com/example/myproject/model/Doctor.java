package com.example.myproject.model;

import java.util.List;
import java.util.ArrayList;

public class Doctor extends Person {
  private String specialization;
  private List<BloodBank> assignedBanks = new ArrayList<>();

  @Override
  public String getRoleLabel() {
    return "Doctor";
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  // Getter so Thymeleaf can access the data
  public List<BloodBank> getAssignedBanks() {
    return assignedBanks;
  }

  public void setAssignedBanks(List<BloodBank> assignedBanks) {
    this.assignedBanks = assignedBanks;
  }
}