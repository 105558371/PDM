package com.example.myproject.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptUtil {
  private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public static String hashPassword(String plain) {
    return encoder.encode(plain);
  }

  public static boolean checkPassword(String plain, String hashed) {
    return encoder.matches(plain, hashed);
  }
}