package com.example.myproject.repository;

import com.example.myproject.model.BloodBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BloodBankRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<BloodBank> mapper = (rs, rowNum) -> {
    BloodBank b = new BloodBank();
    b.setBankId(rs.getInt("bank_id"));
    b.setBankName(rs.getString("bank_name"));
    b.setBankAddr(rs.getString("bank_addr"));
    return b;
  };

  public List<BloodBank> findAll() {
    String sql = "SELECT * FROM Blood_Banks";
    return jdbc.query(sql, mapper);
  }

  public BloodBank findById(int bankId) {
    String sql = "SELECT * FROM Blood_Banks WHERE bank_id = ?";
    return jdbc.query(sql, mapper, bankId).stream().findFirst().orElse(null);
  }

  public int save(BloodBank bank) {
    String sql = "INSERT INTO Blood_Banks (bank_name, bank_addr) VALUES (?, ?)";
    return jdbc.update(sql, bank.getBankName(), bank.getBankAddr());
  }

  public void update(BloodBank bank) {
    String sql = "UPDATE Blood_Banks SET bank_name = ?, bank_addr = ? WHERE bank_id = ?";
    jdbc.update(sql, bank.getBankName(), bank.getBankAddr(), bank.getBankId());
  }

  public void delete(int bankId) {
    jdbc.update("DELETE FROM Blood_Banks WHERE bank_id = ?", bankId);
  }

  // For search functionality
  public List<BloodBank> searchByNameOrLocation(String keyword) {
    String sql = "SELECT * FROM Blood_Banks WHERE bank_name LIKE ? OR bank_addr LIKE ?";
    String like = "%" + keyword + "%";
    return jdbc.query(sql, mapper, like, like);
  }
}