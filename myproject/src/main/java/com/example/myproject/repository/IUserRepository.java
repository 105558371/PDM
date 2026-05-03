package com.example.myproject.repository;

import com.example.myproject.model.User;

public interface IUserRepository {
  User findByUsername(String username);

  User findById(int userId);

  int save(User user);

  void update(User user);
}