package com.example.myproject.model;

public class BloodBank {
  private int bankId;
  private String bankName;
  private String bankAddr;

  // getters/setters
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

  public String getBankAddr() {
    return bankAddr;
  }

  public void setBankAddr(String bankAddr) {
    this.bankAddr = bankAddr;
  }
}