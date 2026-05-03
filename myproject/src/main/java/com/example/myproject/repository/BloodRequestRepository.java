package com.example.myproject.repository;

import com.example.myproject.model.BloodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BloodRequestRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<BloodRequest> mapper = (rs, rowNum) -> {
    BloodRequest r = new BloodRequest();
    r.setRequestId(rs.getInt("request_id"));
    r.setBloodType(rs.getString("blood_type"));
    r.setQuantity(rs.getInt("quantity"));
    r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
    r.setStatus(rs.getString("status"));
    r.setDoctorId(rs.getInt("doctor_id"));
    r.setBankId(rs.getInt("bank_id") != 0 ? rs.getInt("bank_id") : null);
    r.setDoctorName(rs.getString("doctor_name"));
    r.setBankName(rs.getString("bank_name"));
    return r;
  };

  public List<BloodRequest> findAllPending() {
    String sql = "SELECT r.*, d.doctor_name, b.bank_name FROM Blood_Requests r " +
        "LEFT JOIN Doctors d ON r.doctor_id = d.doctor_id " +
        "LEFT JOIN Blood_Banks b ON r.bank_id = b.bank_id " +
        "WHERE r.status = 'pending'";
    return jdbc.query(sql, mapper);
  }

  public List<BloodRequest> findByDoctorId(int doctorId) {
    String sql = "SELECT r.*, d.doctor_name, b.bank_name FROM Blood_Requests r " +
        "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
        "LEFT JOIN Blood_Banks b ON r.bank_id = b.bank_id " +
        "WHERE r.doctor_id = ?";
    return jdbc.query(sql, mapper, doctorId);
  }

  public BloodRequest findById(int requestId) {
    String sql = "SELECT r.*, d.doctor_name, b.bank_name FROM Blood_Requests r " +
        "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
        "LEFT JOIN Blood_Banks b ON r.bank_id = b.bank_id " +
        "WHERE r.request_id = ?";
    return jdbc.query(sql, mapper, requestId).stream().findFirst().orElse(null);
  }

  public void save(BloodRequest request) {
    String sql = "INSERT INTO Blood_Requests (blood_type, quantity, doctor_id, status) VALUES (?, ?, ?, 'pending')";
    jdbc.update(sql, request.getBloodType(), request.getQuantity(), request.getDoctorId());
  }

  public void updateStatusAndBank(int requestId, String status, Integer bankId) {
    String sql = "UPDATE Blood_Requests SET status = ?, bank_id = ? WHERE request_id = ?";
    jdbc.update(sql, status, bankId, requestId);
  }
  
  public List<BloodRequest> findAllFiltered(String status, String bloodType) {
    StringBuilder sql = new StringBuilder(
        "SELECT r.*, d.doctor_name, b.bank_name FROM Blood_Requests r " +
            "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
            "LEFT JOIN Blood_Banks b ON r.bank_id = b.bank_id WHERE 1=1");
    if (status != null && !status.isEmpty())
      sql.append(" AND r.status = '").append(status).append("'");
    if (bloodType != null && !bloodType.isEmpty())
      sql.append(" AND r.blood_type = '").append(bloodType).append("'");
    sql.append(" ORDER BY r.request_date DESC");
    return jdbc.query(sql.toString(), mapper);
  }
}