package com.example.myproject.repository;

import com.example.myproject.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class DoctorRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<Doctor> mapper = (rs, rowNum) -> {
    Doctor d = new Doctor();
    d.setId(rs.getInt("doctor_id"));
    d.setUserId(rs.getInt("user_id"));
    d.setName(rs.getString("doctor_name"));
    d.setSpecialization(rs.getString("specialization"));
    return d;
  };

  public Doctor findByUserId(int userId) {
    String sql = "SELECT * FROM Doctors WHERE user_id = ?";
    return jdbc.query(sql, mapper, userId).stream().findFirst().orElse(null);
  }

  public Doctor findById(int doctorId) {
    String sql = "SELECT * FROM Doctors WHERE doctor_id = ?";
    return jdbc.query(sql, mapper, doctorId).stream().findFirst().orElse(null);
  }

  public List<Doctor> findAll() {
    String sql = "SELECT * FROM Doctors";
    return jdbc.query(sql, mapper);
  }

  public int save(Doctor doctor) {
    String sql = "INSERT INTO Doctors (user_id, doctor_name, specialization) VALUES (?, ?, ?)";
    return jdbc.update(sql, doctor.getUserId(), doctor.getName(), doctor.getSpecialization());
  }

  public void updateSpecialization(int doctorId, String specialization) {
    String sql = "UPDATE Doctors SET specialization = ? WHERE doctor_id = ?";
    jdbc.update(sql, specialization, doctorId);
  }

  public void delete(int doctorId) {
    jdbc.update("DELETE FROM Doctors WHERE doctor_id = ?", doctorId);
  }

  // Get bank IDs where this doctor works
  public List<Integer> findBankIdsByDoctorId(int doctorId) {
    String sql = "SELECT bank_id FROM Works_At WHERE doctor_id = ?";
    return jdbc.queryForList(sql, Integer.class, doctorId);
  }

  public List<Doctor> searchByNameOrSpecialization(String keyword) {
    String sql = "SELECT * FROM Doctors WHERE doctor_name LIKE ? OR specialization LIKE ?";
    String like = "%" + keyword + "%";
    return jdbc.query(sql, mapper, like, like);
  }
}