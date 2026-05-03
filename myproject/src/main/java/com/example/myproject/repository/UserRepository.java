package com.example.myproject.repository;

import com.example.myproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements IUserRepository {
  @Autowired
  private JdbcTemplate jdbc;

  private RowMapper<User> mapper = (rs, rowNum) -> {
    User u = new User();
    u.setUserId(rs.getInt("user_id"));
    u.setUsername(rs.getString("username"));
    u.setPassword(rs.getString("password"));
    u.setRole(rs.getString("role"));
    return u;
  };

  @Override
  public User findByUsername(String username) {
    String sql = "SELECT * FROM Users WHERE username = ?";
    return jdbc.query(sql, mapper, username).stream().findFirst().orElse(null);
  }

  @Override
  public User findById(int userId) {
    String sql = "SELECT * FROM Users WHERE user_id = ?";
    return jdbc.query(sql, mapper, userId).stream().findFirst().orElse(null);
  }

  @Override
  public int save(User user) {
    String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
    return jdbc.update(sql, user.getUsername(), user.getPassword(), user.getRole());
  }

  @Override
  public void update(User user) {
    String sql = "UPDATE Users SET username = ?, password = ? WHERE user_id = ?";
    jdbc.update(sql, user.getUsername(), user.getPassword(), user.getUserId());
  }
}