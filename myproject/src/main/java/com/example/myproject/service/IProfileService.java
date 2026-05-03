package com.example.myproject.service;

public interface IProfileService {
  void updateProfile(int userId, String newContact, String newUsername, String newPassword);
}