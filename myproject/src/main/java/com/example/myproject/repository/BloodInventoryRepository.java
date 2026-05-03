package com.example.myproject.repository;

import com.example.myproject.model.BloodInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class BloodInventoryRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<BloodInventory> mapper = (rs, rowNum) -> {
    BloodInventory bi = new BloodInventory();
    bi.setInventoryId(rs.getInt("inventory_id"));
    bi.setBloodType(rs.getString("blood_type"));
    bi.setExpdate(rs.getDate("expdate") != null ? rs.getDate("expdate").toLocalDate() : null);
    bi.setStatus(rs.getString("status"));
    bi.setBankId(rs.getInt("bank_id"));
    bi.setBankName(rs.getString("bank_name"));
    bi.setDonationId(rs.getInt("donation_id"));
    return bi;
  };

  public List<BloodInventory> findByBankId(int bankId) {
    String sql = "SELECT i.*, b.bank_name FROM Blood_Inventory i JOIN Blood_Banks b ON i.bank_id = b.bank_id WHERE i.bank_id = ?";
    return jdbc.query(sql, mapper, bankId);
  }

  public void updateStatus(int inventoryId, String status) {
    jdbc.update("UPDATE Blood_Inventory SET status = ? WHERE inventory_id = ?", status, inventoryId);
  }

  public void save(BloodInventory inventory) {
    String sql = "INSERT INTO Blood_Inventory (blood_type, status, bank_id, donation_id) VALUES (?, ?, ?, ?)";
    jdbc.update(sql, inventory.getBloodType(), inventory.getStatus(), inventory.getBankId(), inventory.getDonationId());
    // expdate is set by trigger
  }

  // Custom SQL execution (for dynamic filters)
  public List<BloodInventory> findByCustomSql(String sql) {
    RowMapper<BloodInventory> mapper = (rs, rowNum) -> {
      BloodInventory bi = new BloodInventory();
      bi.setInventoryId(rs.getInt("inventory_id"));
      bi.setBloodType(rs.getString("blood_type"));
      bi.setExpdate(rs.getDate("expdate") != null ? rs.getDate("expdate").toLocalDate() : null);
      bi.setStatus(rs.getString("status"));
      bi.setBankId(rs.getInt("bank_id"));
      bi.setBankName(rs.getString("bank_name"));
      bi.setDonationId(rs.getInt("donation_id"));
      return bi;
    };
    return jdbc.query(sql, mapper);
  }
  
  // Count available bags for a specific bank and blood type (or all blood types if null)
  public int countAvailableByBankAndType(int bankId, String bloodType) {
    String sql = "SELECT COUNT(*) FROM Blood_Inventory WHERE bank_id = ? AND status = 'available'";
    if (bloodType != null && !bloodType.isEmpty()) {
      sql += " AND blood_type = ?";
      return jdbc.queryForObject(sql, Integer.class, bankId, bloodType);
    }
    return jdbc.queryForObject(sql, Integer.class, bankId);
  }

  // Find available bags by bank and blood type, ordered by expiry (oldest first)
  public List<BloodInventory> findAvailableByTypeAndBankOrderByExpiry(String bloodType, int bankId) {
    String sql = "SELECT i.*, b.bank_name FROM Blood_Inventory i " +
        "JOIN Blood_Banks b ON i.bank_id = b.bank_id " +
        "WHERE i.status = 'available' AND i.blood_type = ? AND i.bank_id = ? " +
        "ORDER BY i.expdate ASC";
    return jdbc.query(sql, (rs, rowNum) -> {
      BloodInventory bi = new BloodInventory();
      bi.setInventoryId(rs.getInt("inventory_id"));
      bi.setBloodType(rs.getString("blood_type"));
      bi.setExpdate(rs.getDate("expdate") != null ? rs.getDate("expdate").toLocalDate() : null);
      bi.setStatus(rs.getString("status"));
      bi.setBankId(rs.getInt("bank_id"));
      bi.setBankName(rs.getString("bank_name"));
      bi.setDonationId(rs.getInt("donation_id"));
      return bi;
    }, bloodType, bankId);
  }

  public List<BloodInventory> findByCustomSqlWithDateParam(String sql, LocalDate dateParam) {
    RowMapper<BloodInventory> mapper = (rs, rowNum) -> {
      BloodInventory bi = new BloodInventory();
      bi.setInventoryId(rs.getInt("inventory_id"));
      bi.setBloodType(rs.getString("blood_type"));
      bi.setExpdate(rs.getDate("expdate") != null ? rs.getDate("expdate").toLocalDate() : null);
      bi.setStatus(rs.getString("status"));
      bi.setBankId(rs.getInt("bank_id"));
      bi.setBankName(rs.getString("bank_name"));
      bi.setDonationId(rs.getInt("donation_id"));
      return bi;
    };
    return jdbc.query(sql, mapper, dateParam);
  }
}