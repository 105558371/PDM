package com.example.myproject.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BloodExpiryScheduler {

  @Autowired
  private JdbcTemplate jdbc;

  // Runs once on app startup to catch any bags that expired during downtime
  @PostConstruct
  public void catchUpOnStartup() {
    int updated = jdbc.update(
        "UPDATE Blood_Inventory SET status = 'expired' " +
        "WHERE expdate < CURDATE() AND status = 'available'");
    System.out.println("[Startup] Caught up " + updated + " expired blood unit(s).");
  }

  // Runs every day at midnight
  @Scheduled(cron = "0 0 0 * * *")
  public void markExpiredBloodUnits() {
    int updated = jdbc.update(
        "UPDATE Blood_Inventory SET status = 'expired' " +
        "WHERE expdate < CURDATE() AND status = 'available'");
    System.out.println("[Scheduler] Marked " + updated + " blood unit(s) as expired.");
  }
}