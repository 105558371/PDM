package com.example.myproject.controller;

import com.example.myproject.model.Donor;
import com.example.myproject.model.User;
import com.example.myproject.service.DonorService;
import com.example.myproject.service.DonationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/donor")
public class DonorController {

  @Autowired
  private DonorService donorService;

  @Autowired
  private DonationService donationService;

  // Role check interceptor
  @ModelAttribute
  public void checkAuth(HttpSession session, Model model) {
    User user = (User) session.getAttribute("loggedUser");
    if (user == null || !"donor".equals(user.getRole())) {
      throw new RuntimeException("Access denied. Donors only.");
    }
    model.addAttribute("user", user);
    Donor donor = donorService.findByUserId(user.getUserId());
    model.addAttribute("donor", donor);
  }

  // Dashboard showing donor profile summary
  @GetMapping("/dashboard")
  public String dashboard(Model model, HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    Donor donor = donorService.findByUserId(user.getUserId());
    model.addAttribute("donor", donor);
    return "donor/dashboard";
  }

  // View own donation history
  @GetMapping("/history")
  public String donationHistory(Model model, HttpSession session) {
    User user = (User) session.getAttribute("loggedUser");
    Donor donor = donorService.findByUserId(user.getUserId());
    model.addAttribute("donations", donationService.getDonationHistory(donor.getId()));
    return "donor/history";
  }

  // Edit profile: username, password, contact (phone)
  @GetMapping("/edit-profile")
  public String editProfileForm() {
    return "donor/edit-profile";
  }

  @PostMapping("/edit-profile")
  public String updateProfile(@RequestParam String username,
      @RequestParam(required = false) String password,
      @RequestParam String contact,
      HttpSession session,
      RedirectAttributes ra) {
    User user = (User) session.getAttribute("loggedUser");
    donorService.updateProfile(user.getUserId(), contact, username, password);
    // Update session attributes
    user.setUsername(username);
    session.setAttribute("loggedUser", user);
    ra.addFlashAttribute("success", "Profile updated successfully.");
    return "redirect:/donor/dashboard";
  }
}