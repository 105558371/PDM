package com.example.myproject.service;

import com.example.myproject.exception.BusinessException;
import com.example.myproject.model.*;
import com.example.myproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {
  @Autowired
  private BloodRequestRepository requestRepo;
  @Autowired
  private BloodInventoryRepository inventoryRepo;
  @Autowired
  private BloodBankRepository bankRepo;
  @Autowired
  private DonorRepository donorRepo;
  @Autowired
  private DoctorRepository doctorRepo;
  @Autowired
  private BloodDonationRepository donationRepo;
  @Autowired
  private WorksAtRepository worksAtRepo;

  @Transactional
  public void approveRequest(int requestId, int bankId) throws BusinessException {
    BloodRequest req = requestRepo.findById(requestId);
    if (req == null)
      throw new BusinessException("Request not found");
    if (!"pending".equals(req.getStatus()))
      throw new BusinessException("Request already processed");

    List<BloodInventory> bags = inventoryRepo.findAvailableByTypeAndBankOrderByExpiry(req.getBloodType(), bankId);
    if (bags.size() < req.getQuantity()) {
      throw new BusinessException("Not enough blood bags at selected bank. Available: " + bags.size());
    }

    for (int i = 0; i < req.getQuantity(); i++) {
      inventoryRepo.updateStatus(bags.get(i).getInventoryId(), "used");
    }
    requestRepo.updateStatusAndBank(requestId, "approved", bankId);
  }

  public void rejectRequest(int requestId) {
    requestRepo.updateStatusAndBank(requestId, "rejected", null);
  }

  public List<BloodRequest> getPendingRequests() {
    return requestRepo.findAllPending();
  }

  @Transactional
  public void recordDonation(int donorId, String donateAddr, int bankId, String bloodType, int numberOfBags) {
    // Insert into Blood_Donations
    BloodDonation donation = new BloodDonation();
    donation.setDonorId(donorId);
    donation.setDonateAddr(donateAddr);
    donation.setDate(java.time.LocalDateTime.now());
    int donationId = donationRepo.saveAndReturnId(donation);

    // Insert each bag into Blood_Inventory
    for (int i = 0; i < numberOfBags; i++) {
      BloodInventory bag = new BloodInventory();
      bag.setBloodType(bloodType);
      bag.setStatus("available");
      bag.setBankId(bankId);
      bag.setDonationId(donationId);
      inventoryRepo.save(bag);
    }
  }

  public List<BloodInventory> getInventoryFiltered(String bloodType, String status, String sortExp) {
    StringBuilder sql = new StringBuilder(
        "SELECT i.*, b.bank_name FROM Blood_Inventory i JOIN Blood_Banks b ON i.bank_id = b.bank_id WHERE 1=1");
    if (bloodType != null && !bloodType.isEmpty())
      sql.append(" AND i.blood_type = '").append(bloodType).append("'");
    if (status != null && !status.isEmpty())
      sql.append(" AND i.status = '").append(status).append("'");
    if ("asc".equalsIgnoreCase(sortExp))
      sql.append(" ORDER BY i.expdate ASC");
    else if ("desc".equalsIgnoreCase(sortExp))
      sql.append(" ORDER BY i.expdate DESC");
    else
      sql.append(" ORDER BY i.expdate ASC");
    return inventoryRepo.findByCustomSql(sql.toString());
  }

  public List<BloodBank> getAllBloodBanks() {
    return bankRepo.findAll();
  }

  public List<Donor> getAllDonors() {
    return donorRepo.findAll();
  }

  public List<Doctor> getAllDoctors() {
    return doctorRepo.findAll();
  }

  public void assignDoctorToBank(int doctorId, int bankId) {
    worksAtRepo.assign(doctorId, bankId);
  }

  public Map<String, Integer> getLowStockWarnings() {
    // threshold = 5 bags
    List<BloodBank> banks = bankRepo.findAll();
    return banks.stream().collect(Collectors.toMap(
        b -> b.getBankName(),
        b -> inventoryRepo.countAvailableByBankAndType(b.getBankId(), null))).entrySet().stream()
        .filter(e -> e.getValue() < 5)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}