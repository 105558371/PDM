package com.example.myproject.service;

import com.example.myproject.model.User;
import com.example.myproject.model.Donor;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.repository.DonorRepository;
import com.example.myproject.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private DonorRepository donorRepo;

  public User authenticate(String username, String password) {
    User user = userRepo.findByUsername(username);
    if (user != null && BCryptUtil.checkPassword(password, user.getPassword())) {
      return user;
    }
    return null;
  }

  @Transactional
  public void registerDonor(String username, String rawPassword, String fullName, String contact, String bloodType) {
    if (userRepo.findByUsername(username) != null) {
      throw new RuntimeException("Username already exists");
    }
    User user = new User();
    user.setUsername(username);
    user.setPassword(BCryptUtil.hashPassword(rawPassword));
    user.setRole("donor");
    userRepo.save(user);

    // fetch the auto-generated user_id (simplified: need to get last insert id)
    User created = userRepo.findByUsername(username);
    Donor donor = new Donor();
    donor.setUserId(created.getUserId());
    donor.setName(fullName);
    donor.setContact(contact);
    donor.setBloodType(bloodType);
    donor.setLastDonate(null);
    donorRepo.save(donor);
  }
}