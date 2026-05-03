package com.example.myproject.controller;

import com.example.myproject.model.Doctor;
import com.example.myproject.model.User;
import com.example.myproject.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

  @Autowired
  private DoctorService doctorService;

  // Role check interceptor
  @ModelAttribute
  public void checkAuth(HttpSession session, Model model) {
    User user = (User) session.getAttribute("loggedUser");
    if (user == null || !"doctor".equals(user.getRole())) {
      throw new RuntimeException("Access denied. Doctors only.");
    }
    model.addAttribute("user", user);
    Doctor doctor = doctorService.findByUserId(user.getUserId());
    model.addAttribute("doctor", doctor);
  }

  // Dashboard
  @GetMapping("/dashboard")
  public String dashboard() {
    return "doctor/dashboard";
  }

  // View available inventory (no donor names – only blood type, expiry, bank)
  @GetMapping("/inventory")
  public String viewInventory(@RequestParam(required = false) String bloodType,
      Model model,
      HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    Doctor doctor = doctorService.findByUserId(user.getUserId());
    model.addAttribute("bags", doctorService.getAvailableInventory(doctor, bloodType));
    model.addAttribute("selectedBloodType", bloodType);
    return "doctor/inventory";
  }

  // Show form to create a new blood request
  @GetMapping("/new-request")
  public String newRequestForm() {
    return "doctor/new-request";
  }

  // Submit a new request
  @PostMapping("/new-request")
  public String createRequest(@RequestParam String bloodType,
      @RequestParam int quantity,
      HttpSession session,
      RedirectAttributes ra) {
    User user = (User) session.getAttribute("loggedUser");
    Doctor doctor = doctorService.findByUserId(user.getUserId());
    doctorService.createRequest(doctor.getId(), bloodType, quantity);
    ra.addFlashAttribute("success", "Blood request submitted.");
    return "redirect:/doctor/my-requests";
  }

  // View doctor's own request history
  @GetMapping("/my-requests")
  public String myRequests(Model model, HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    Doctor doctor = doctorService.findByUserId(user.getUserId());
    model.addAttribute("requests", doctorService.getMyRequests(doctor.getId()));
    return "doctor/my-requests";
  }

  // Edit profile (username & password)
  @GetMapping("/edit-profile")
  public String editProfileForm() {
    return "doctor/edit-profile";
  }

  @PostMapping("/edit-profile")
  public String updateProfile(@RequestParam String username,
      @RequestParam(required = false) String password,
      HttpSession session,
      RedirectAttributes ra) {
    User user = (User) session.getAttribute("loggedUser");
    doctorService.updateProfile(user.getUserId(), null, username, password);
    // Update session username if changed
    user.setUsername(username);
    session.setAttribute("loggedUser", user);
    ra.addFlashAttribute("success", "Profile updated.");
    return "redirect:/doctor/dashboard";
  }
}