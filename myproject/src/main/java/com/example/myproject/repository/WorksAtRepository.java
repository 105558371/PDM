package com.example.myproject.repository;

import com.example.myproject.model.BloodBank;
import com.example.myproject.model.WorksAt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class WorksAtRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<WorksAt> mapper = (rs, rowNum) -> {
    WorksAt wa = new WorksAt();
    wa.setDoctorId(rs.getInt("doctor_id"));
    wa.setBankId(rs.getInt("bank_id"));
    return wa;
  };

  public void assign(int doctorId, int bankId) {
    String sql = "INSERT INTO Works_At (doctor_id, bank_id) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE doctor_id = doctor_id";
    jdbc.update(sql, doctorId, bankId);
  }

  public void removeAssignment(int doctorId, int bankId) {
    jdbc.update("DELETE FROM Works_At WHERE doctor_id = ? AND bank_id = ?", doctorId, bankId);
  }

  public List<WorksAt> findByDoctorId(int doctorId) {
    String sql = "SELECT * FROM Works_At WHERE doctor_id = ?";
    return jdbc.query(sql, mapper, doctorId);
  }

  public List<WorksAt> findByBankId(int bankId) {
    String sql = "SELECT * FROM Works_At WHERE bank_id = ?";
    return jdbc.query(sql, mapper, bankId);
  }

  public List<Integer> findDoctorIdsByBankId(int bankId) {
    String sql = "SELECT doctor_id FROM Works_At WHERE bank_id = ?";
    return jdbc.queryForList(sql, Integer.class, bankId);
  }

  public List<Integer> findBankIdsByDoctorId(int doctorId) {
    String sql = "SELECT bank_id FROM Works_At WHERE doctor_id = ?";
    return jdbc.queryForList(sql, Integer.class, doctorId);
  }

  public List<BloodBank> findBanksByDoctorId(int doctorId) {
    String sql = "SELECT b.* FROM Blood_Banks b JOIN Works_At w ON b.bank_id = w.bank_id WHERE w.doctor_id = ?";
    return jdbc.query(sql, (rs, rowNum) -> {
      BloodBank b = new BloodBank();
      b.setBankId(rs.getInt("bank_id"));
      b.setBankName(rs.getString("bank_name"));
      b.setBankAddr(rs.getString("bank_addr"));
      return b;
    }, doctorId);
  }
}