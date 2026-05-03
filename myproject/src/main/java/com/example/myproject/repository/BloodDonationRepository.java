package com.example.myproject.repository;

import com.example.myproject.model.BloodDonation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BloodDonationRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<BloodDonation> mapper = (rs, rowNum) -> {
    BloodDonation d = new BloodDonation();
    d.setDonationId(rs.getInt("donation_id"));
    d.setDate(rs.getTimestamp("date").toLocalDateTime());
    d.setDonateAddr(rs.getString("donate_addr"));
    d.setDonorId(rs.getInt("donor_id"));
    d.setDonorName(rs.getString("donor_name"));
    return d;
  };

  public List<BloodDonation> findByDonorId(int donorId) {
    String sql = "SELECT d.*, don.donor_name FROM Blood_Donations d " +
        "JOIN Donors don ON d.donor_id = don.donor_id " +
        "WHERE d.donor_id = ? ORDER BY d.date DESC";
    return jdbc.query(sql, mapper, donorId);
  }

  public BloodDonation findById(int donationId) {
    String sql = "SELECT d.*, don.donor_name FROM Blood_Donations d " +
        "JOIN Donors don ON d.donor_id = don.donor_id " +
        "WHERE d.donation_id = ?";
    return jdbc.query(sql, mapper, donationId).stream().findFirst().orElse(null);
  }

  public List<BloodDonation> findAll() {
    String sql = "SELECT d.*, don.donor_name FROM Blood_Donations d " +
        "JOIN Donors don ON d.donor_id = don.donor_id ORDER BY d.date DESC";
    return jdbc.query(sql, mapper);
  }

  // Save and return auto-generated donation_id
  public int saveAndReturnId(BloodDonation donation) {
    String sql = "INSERT INTO Blood_Donations (date, donate_addr, donor_id) VALUES (?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setTimestamp(1,
          java.sql.Timestamp.valueOf(donation.getDate() != null ? donation.getDate() : LocalDateTime.now()));
      ps.setString(2, donation.getDonateAddr());
      ps.setInt(3, donation.getDonorId());
      return ps;
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  // For admin: filter by date range, donor name etc.
  public List<BloodDonation> search(String donorName, LocalDateTime fromDate, LocalDateTime toDate) {
    StringBuilder sql = new StringBuilder(
        "SELECT d.*, don.donor_name FROM Blood_Donations d " +
            "JOIN Donors don ON d.donor_id = don.donor_id WHERE 1=1");
    if (donorName != null && !donorName.isEmpty())
      sql.append(" AND don.donor_name LIKE '%").append(donorName).append("%'");
    if (fromDate != null)
      sql.append(" AND d.date >= '").append(fromDate).append("'");
    if (toDate != null)
      sql.append(" AND d.date <= '").append(toDate).append("'");
    sql.append(" ORDER BY d.date DESC");
    return jdbc.query(sql.toString(), mapper);
  }
}