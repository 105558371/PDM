package com.example.myproject.repository;

import com.example.myproject.model.Donor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DonorRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<Donor> mapper = (rs, rowNum) -> {
    Donor d = new Donor();
    d.setId(rs.getInt("donor_id"));
    d.setUserId(rs.getInt("user_id"));
    d.setName(rs.getString("donor_name"));
    d.setContact(rs.getString("contact"));
    d.setBloodType(rs.getString("blood_type"));
    // Safe conversion: if last_donate is NULL, set to null in object
    java.sql.Timestamp ts = rs.getTimestamp("last_donate");
    d.setLastDonate(ts != null ? ts.toLocalDateTime() : null);
    return d;
  };

  public Donor findByUserId(int userId) {
    String sql = "SELECT * FROM Donors WHERE user_id = ?";
    return jdbc.query(sql, mapper, userId).stream().findFirst().orElse(null);
  }

  public Donor findById(int donorId) {
    String sql = "SELECT * FROM Donors WHERE donor_id = ?";
    return jdbc.query(sql, mapper, donorId).stream().findFirst().orElse(null);
  }

  public List<Donor> findAll() {
    String sql = "SELECT * FROM Donors";
    return jdbc.query(sql, mapper);
  }

  public int save(Donor donor) {
    String sql = "INSERT INTO Donors (user_id, donor_name, contact, blood_type, last_donate) VALUES (?, ?, ?, ?, ?)";
    return jdbc.update(sql, donor.getUserId(), donor.getName(), donor.getContact(), donor.getBloodType(),
        donor.getLastDonate());
  }

  public void updateContact(int donorId, String contact) {
    String sql = "UPDATE Donors SET contact = ? WHERE donor_id = ?";
    jdbc.update(sql, contact, donorId);
  }

  public void delete(int donorId) {
    // 1. Get user_id before deleting donor
    Integer userId = null;
    try {
      userId = jdbc.queryForObject(
          "SELECT user_id FROM Donors WHERE donor_id = ?",
          Integer.class, donorId);
    } catch (Exception e) {
      // donor doesn't exist – nothing to delete
      return;
    }

    // 2. Delete Blood_Inventory linked to this donor's donations
    jdbc.update(
        "DELETE bi FROM Blood_Inventory bi " +
            "JOIN Blood_Donations bd ON bi.donation_id = bd.donation_id " +
            "WHERE bd.donor_id = ?",
        donorId);

    // 3. Delete Blood_Donations
    jdbc.update("DELETE FROM Blood_Donations WHERE donor_id = ?", donorId);

    // 4. Delete Donor
    jdbc.update("DELETE FROM Donors WHERE donor_id = ?", donorId);

    // 5. Delete User account (foreign key from Donors to Users is now gone)
    if (userId != null) {
      jdbc.update("DELETE FROM Users WHERE user_id = ?", userId);
    }
  }

  public List<Donor> searchByNameOrContact(String keyword) {
    String sql = "SELECT * FROM Donors WHERE donor_name LIKE ? OR contact LIKE ?";
    String like = "%" + keyword + "%";
    return jdbc.query(sql, mapper, like, like);
  }

  public void updateLastDonateDate(int donorId, LocalDateTime donationDateTime) {
    String sql = "UPDATE Donors SET last_donate = ? WHERE donor_id = ?";
    jdbc.update(sql, java.sql.Date.valueOf(donationDateTime.toLocalDate()), donorId);
  }
}