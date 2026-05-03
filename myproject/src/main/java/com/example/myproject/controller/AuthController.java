package com.example.myproject.controller;

import com.example.myproject.model.User;
import com.example.myproject.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

  @Autowired
  private AuthService authService;

  // Display login page
  @GetMapping("/")
  public String redirectToLogin() {
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String loginForm() {
    return "login";
  }

  // Process login
  @PostMapping("/login")
  public String login(@RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      Model model) {
    User user = authService.authenticate(username, password);
    if (user == null) {
      model.addAttribute("error", "Invalid username or password");
      return "login";
    }
    session.setAttribute("loggedUser", user);
    // Redirect based on role
    switch (user.getRole()) {
      case "donor":
        return "redirect:/donor/dashboard";
      case "doctor":
        return "redirect:/doctor/dashboard";
      case "admin":
        return "redirect:/admin/dashboard";
      default:
        return "redirect:/login";
    }
  }

  // Display sign-up page (for donors)
  @GetMapping("/signup")
  public String signupForm() {
    return "signup";
  }

  // Process donor registration
  @PostMapping("/signup")
  public String signup(@RequestParam String username,
      @RequestParam String password,
      @RequestParam String fullName,
      @RequestParam String contact,
      @RequestParam String bloodType,
      Model model) {
    try {
      authService.registerDonor(username, password, fullName, contact, bloodType);
      model.addAttribute("message", "Account created successfully! Please login.");
      return "login";
    } catch (RuntimeException e) {
      model.addAttribute("error", e.getMessage());
      return "signup";
    }
  }

  // Logout
  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
  }
}