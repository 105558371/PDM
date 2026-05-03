package com.example.myproject.service;

import com.example.myproject.model.Doctor;
import com.example.myproject.model.User;
import com.example.myproject.model.BloodInventory;
import com.example.myproject.model.BloodRequest;
import com.example.myproject.repository.DoctorRepository;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.repository.BloodInventoryRepository;
import com.example.myproject.repository.BloodRequestRepository;
import com.example.myproject.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService implements IProfileService {
  @Autowired
  private DoctorRepository doctorRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private BloodInventoryRepository inventoryRepo;
  @Autowired
  private BloodRequestRepository requestRepo;

  public Doctor findByUserId(int userId) {
    return doctorRepo.findByUserId(userId);
  }

  public List<BloodInventory> getAvailableInventory(Doctor doctor, String bloodTypeFilter) {
    // Get banks where doctor works
    List<Integer> bankIds = doctorRepo.findBankIdsByDoctorId(doctor.getId());
    if (bankIds.isEmpty())
      return List.of();
    String sql = "SELECT i.*, b.bank_name FROM Blood_Inventory i JOIN Blood_Banks b ON i.bank_id = b.bank_id " +
        "WHERE i.status = 'available' AND i.bank_id IN (" +
        bankIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("0") + ")";
    if (bloodTypeFilter != null && !bloodTypeFilter.isEmpty()) {
      sql += " AND i.blood_type = '" + bloodTypeFilter + "'";
    }
    sql += " ORDER BY i.expdate ASC";
    return inventoryRepo.findByCustomSql(sql);
  }

  public void createRequest(int doctorId, String bloodType, int quantity) {
    BloodRequest req = new BloodRequest();
    req.setBloodType(bloodType);
    req.setQuantity(quantity);
    req.setDoctorId(doctorId);
    requestRepo.save(req);
  }

  public List<BloodRequest> getMyRequests(int doctorId) {
    return requestRepo.findByDoctorId(doctorId);
  }

  @Override
  public void updateProfile(int userId, String newContact, String newUsername, String newPassword) {
    // Doctor profile has no contact field in this design – ignore contact
    User user = userRepo.findById(userId);
    user.setUsername(newUsername);
    if (newPassword != null && !newPassword.isEmpty()) {
      user.setPassword(BCryptUtil.hashPassword(newPassword));
    }
    userRepo.update(user);
  }
}