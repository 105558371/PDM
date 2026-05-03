package com.example.myproject.service;

import com.example.myproject.model.Donor;
import com.example.myproject.model.User;
import com.example.myproject.repository.DonorRepository;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.myproject.model.BloodDonation;
import com.example.myproject.repository.BloodDonationRepository;

@Service
public class DonorService implements IProfileService {
  @Autowired
  private DonorRepository donorRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private BloodDonationRepository donationRepo;

  public Donor findByUserId(int userId) {
    return donorRepo.findByUserId(userId);
  }

  public List<BloodDonation> getDonationHistory(int donorId) {
    return donationRepo.findByDonorId(donorId);
  }

  @Override
  public void updateProfile(int userId, String newContact, String newUsername, String newPassword) {
    Donor donor = donorRepo.findByUserId(userId);
    donorRepo.updateContact(donor.getId(), newContact);

    User user = userRepo.findById(userId);
    user.setUsername(newUsername);
    if (newPassword != null && !newPassword.isEmpty()) {
      user.setPassword(BCryptUtil.hashPassword(newPassword));
    }
    userRepo.update(user);
  }
}