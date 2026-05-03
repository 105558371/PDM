package com.example.myproject.service;

import com.example.myproject.model.BloodInventory;
import com.example.myproject.repository.BloodInventoryRepository;
import com.example.myproject.repository.BloodBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class InventoryService {

  @Autowired
  private BloodInventoryRepository inventoryRepository;

  @Autowired
  private BloodBankRepository bankRepository;

  /**
   * Get all blood bags with optional filters.
   * 
   * @param bloodType filter by blood type (e.g., "A+"), null for all
   * @param status    filter by status ("available", "used", "expired"), null for
   *                  all
   * @param sortExp   "asc" or "desc" for expiration date sorting, null for
   *                  default (asc)
   * @return list of matching BloodInventory objects (with bank name populated)
   */
  public List<BloodInventory> getInventoryFiltered(String bloodType, String status, String sortExp) {
    StringBuilder sql = new StringBuilder(
        "SELECT i.*, b.bank_name FROM Blood_Inventory i " +
            "JOIN Blood_Banks b ON i.bank_id = b.bank_id WHERE 1=1");
    if (bloodType != null && !bloodType.isEmpty()) {
      sql.append(" AND i.blood_type = '").append(bloodType).append("'");
    }
    if (status != null && !status.isEmpty()) {
      sql.append(" AND i.status = '").append(status).append("'");
    }
    if ("asc".equalsIgnoreCase(sortExp)) {
      sql.append(" ORDER BY i.expdate ASC");
    } else if ("desc".equalsIgnoreCase(sortExp)) {
      sql.append(" ORDER BY i.expdate DESC");
    } else {
      sql.append(" ORDER BY i.expdate ASC");
    }
    return inventoryRepository.findByCustomSql(sql.toString());
  }

  /**
   * Count available blood bags for a specific bank and blood type.
   * 
   * @param bankId    the blood bank ID
   * @param bloodType the blood type (e.g., "O+")
   * @return number of available bags
   */
  public int countAvailableAtBank(int bankId, String bloodType) {
    return inventoryRepository.countAvailableByBankAndType(bankId, bloodType);
  }

  /**
   * Check if a blood bank has low stock (default threshold = 5 bags) for any
   * blood type.
   * 
   * @return map of bank name -> total available bags (only banks below threshold)
   */
  public Map<String, Integer> getLowStockWarnings() {
    Map<String, Integer> lowStockBanks = new HashMap<>();
    var banks = bankRepository.findAll();
    for (var bank : banks) {
      int totalAvailable = inventoryRepository.countAvailableByBankAndType(bank.getBankId(), null);
      if (totalAvailable < 5) { // threshold = 5 bags
        lowStockBanks.put(bank.getBankName(), totalAvailable);
      }
    }
    return lowStockBanks;
  }

  /**
   * Get a list of blood bags that are about to expire within the next N days
   * (default 7).
   * 
   * @param daysThreshold number of days from now
   * @return list of BloodInventory objects expiring within that period (status =
   *         'available')
   */
  public List<BloodInventory> getExpiringSoon(int daysThreshold) {
    LocalDate threshold = LocalDate.now().plusDays(daysThreshold);
    String sql = "SELECT i.*, b.bank_name FROM Blood_Inventory i " +
        "JOIN Blood_Banks b ON i.bank_id = b.bank_id " +
        "WHERE i.status = 'available' AND i.expdate <= ? " +
        "ORDER BY i.expdate ASC";
    return inventoryRepository.findByCustomSqlWithDateParam(sql, threshold);
  }

  /**
   * Get inventory summary grouped by blood type for a specific bank.
   * 
   * @param bankId the bank ID
   * @return map of blood type -> available count
   */
  public Map<String, Integer> getInventorySummaryByBank(int bankId) {
    Map<String, Integer> summary = new HashMap<>();
    String[] bloodTypes = { "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-" };
    for (String type : bloodTypes) {
      int count = inventoryRepository.countAvailableByBankAndType(bankId, type);
      if (count > 0) {
        summary.put(type, count);
      }
    }
    return summary;
  }
}