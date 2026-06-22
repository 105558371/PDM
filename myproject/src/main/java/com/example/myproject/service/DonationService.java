package com.example.myproject.service;

import com.example.myproject.model.BloodDonation;
import com.example.myproject.model.BloodInventory;
import com.example.myproject.repository.BloodDonationRepository;
import com.example.myproject.repository.BloodInventoryRepository;
import com.example.myproject.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationService {

  @Autowired
  private BloodDonationRepository donationRepository;

  @Autowired
  private BloodInventoryRepository inventoryRepository;

  @Autowired
  private DonorRepository donorRepository;

  /**
   * Record a new blood donation event and add the specified number of bags to
   * inventory.
   * Business rule: One donation can produce multiple blood bags (each becomes a
   * row in Blood_Inventory).
   * The last_donate field in Donors is automatically updated by a database
   * trigger (see SQL).
   *
   * @param donorId      the donor who gave blood
   * @param donateAddr   address where donation took place
   * @param bankId       the blood bank where the bags will be stored
   * @param bloodType    blood type of the donated blood
   * @param numberOfBags number of individual bags to create
   * @return the ID of the created Blood_Donations record
   */
  @Transactional
  public int recordDonation(int donorId, String donateAddr, int bankId, String bloodType, int numberOfBags) {
    // 1. Insert into Blood_Donations
    BloodDonation donation = new BloodDonation();
    donation.setDonorId(donorId);
    donation.setDonateAddr(donateAddr);
    donation.setDate(LocalDateTime.now());
    int donationId = donationRepository.saveAndReturnId(donation);

    // 2. Insert each bag into Blood_Inventory
    for (int i = 0; i < numberOfBags; i++) {
      BloodInventory bag = new BloodInventory();
      bag.setBloodType(bloodType);
      bag.setStatus("available");
      bag.setBankId(bankId);
      bag.setDonationId(donationId);
      inventoryRepository.save(bag);
    }

    // 3. Explicitly update the donor's last donation date
    donorRepository.updateLastDonateDate(donorId, donation.getDate());

    return donationId;
  }

  /**
   * Get donation history for a specific donor.
   * 
   * @param donorId the donor's ID
   * @return list of BloodDonation objects (with donor name populated)
   */
  public List<BloodDonation> getDonationHistory(int donorId) {
    return donationRepository.findByDonorId(donorId);
  }

  /**
   * Get all donations (admin view).
   * 
   * @return list of all BloodDonation records with donor names
   */
  public List<BloodDonation> getAllDonations() {
    return donationRepository.findAll();
  }

  /**
   * Search donations by donor name and/or date range.
   * 
   * @param donorName partial or full donor name (optional)
   * @param fromDate  start date (optional)
   * @param toDate    end date (optional)
   * @return matching donations
   */
  public List<BloodDonation> searchDonations(String donorName, LocalDateTime fromDate, LocalDateTime toDate) {
    return donationRepository.search(donorName, fromDate, toDate);
  }
}