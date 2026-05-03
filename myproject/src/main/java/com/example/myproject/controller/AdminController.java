package com.example.myproject.controller;

import com.example.myproject.exception.BusinessException;
import com.example.myproject.model.*;
import com.example.myproject.service.AdminService;
import com.example.myproject.service.InventoryService;
import com.example.myproject.service.DonationService;
import com.example.myproject.repository.BloodBankRepository;
import com.example.myproject.repository.DoctorRepository;
import com.example.myproject.repository.DonorRepository;
import com.example.myproject.repository.WorksAtRepository;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private AdminService adminService;
  @Autowired
  private InventoryService inventoryService;
  @Autowired
  private DonationService donationService;
  @Autowired
  private DonorRepository donorRepository;
  @Autowired
  private DoctorRepository doctorRepository;
  @Autowired
  private BloodBankRepository bankRepository;
  @Autowired
  private WorksAtRepository worksAtRepository;

  // Role check interceptor
  @ModelAttribute
  public void checkAuth(HttpSession session, Model model) {
    User user = (User) session.getAttribute("loggedUser");
    if (user == null || !"admin".equals(user.getRole())) {
      throw new RuntimeException("Access denied. Admins only.");
    }
    model.addAttribute("admin", user);
  }

  // Dashboard with low stock warnings
  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("lowStock", inventoryService.getLowStockWarnings());
    model.addAttribute("pendingRequestsCount", adminService.getPendingRequests().size());
    return "admin/dashboard";
  }

  // ---------- Donor Management ----------
  @GetMapping("/donors")
  public String listDonors(@RequestParam(required = false) String keyword, Model model) {
    List<Donor> donors;
    if (keyword != null && !keyword.isEmpty()) {
      donors = donorRepository.searchByNameOrContact(keyword);
    } else {
      donors = donorRepository.findAll();
    }
    model.addAttribute("donors", donors);
    return "admin/manage-donors";
  }

  @PostMapping("/donors/delete")
  public String deleteDonor(@RequestParam int donorId, RedirectAttributes ra) {
    donorRepository.delete(donorId);
    ra.addFlashAttribute("success", "Donor deleted.");
    return "redirect:/admin/donors";
  }

  // ---------- Doctor Management ----------
  @GetMapping("/doctors")
  public String listDoctors(@RequestParam(required = false) String keyword, Model model) {
    List<Doctor> doctors;
    if (keyword != null && !keyword.isEmpty()) {
      doctors = doctorRepository.searchByNameOrSpecialization(keyword);
    } else {
      doctors = doctorRepository.findAll();
    }
    // Load assigned banks for each doctor
    for (Doctor d : doctors) {
      List<BloodBank> assigned = worksAtRepository.findBanksByDoctorId(d.getId());
      d.setAssignedBanks(assigned);
    }
    model.addAttribute("doctors", doctors);
    return "admin/manage-doctors";
  }

  @PostMapping("/doctors/assign-bank")
  public String assignBankToDoctor(@RequestParam int doctorId, @RequestParam int bankId, RedirectAttributes ra) {
    adminService.assignDoctorToBank(doctorId, bankId);
    ra.addFlashAttribute("success", "Doctor assigned to blood bank.");
    return "redirect:/admin/doctors";
  }

  @GetMapping("/doctors/assign")
  public String showAssignForm(@RequestParam int doctorId, Model model) {
    Doctor doctor = doctorRepository.findById(doctorId);
    if (doctor == null) {
      return "redirect:/admin/doctors";
    }
    model.addAttribute("doctorId", doctor.getId());
    model.addAttribute("doctorName", doctor.getName());
    model.addAttribute("banks", bankRepository.findAll());
    return "admin/assign-doctor";
  }

  // ---------- Request Approval ----------
  @GetMapping("/requests")
  public String listPendingRequests(Model model) {
    model.addAttribute("requests", adminService.getPendingRequests());
    model.addAttribute("banks", bankRepository.findAll());
    return "admin/approve-requests";
  }

  @PostMapping("/requests/approve")
  public String approveRequest(@RequestParam int requestId,
      @RequestParam int bankId,
      RedirectAttributes ra) {
    try {
      adminService.approveRequest(requestId, bankId);
      ra.addFlashAttribute("success", "Request approved.");
    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/requests";
  }

  @PostMapping("/requests/reject")
  public String rejectRequest(@RequestParam int requestId, RedirectAttributes ra) {
    adminService.rejectRequest(requestId);
    ra.addFlashAttribute("success", "Request rejected.");
    return "redirect:/admin/requests";
  }

  // ---------- Donation Recording ----------
  @GetMapping("/record-donation")
  public String showRecordDonationForm(Model model) {
    model.addAttribute("donors", donorRepository.findAll());
    model.addAttribute("banks", bankRepository.findAll());
    return "admin/record-donation";
  }

  @PostMapping("/record-donation")
  public String recordDonation(@RequestParam int donorId,
      @RequestParam String donateAddr,
      @RequestParam int bankId,
      @RequestParam String bloodType,
      @RequestParam int numberOfBags,
      RedirectAttributes ra) {
    donationService.recordDonation(donorId, donateAddr, bankId, bloodType, numberOfBags);
    ra.addFlashAttribute("success", numberOfBags + " bag(s) added to inventory.");
    return "redirect:/admin/inventory";
  }

  // ---------- Inventory Management ----------
  @GetMapping("/inventory")
  public String viewInventory(@RequestParam(required = false) String bloodType,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String sortExp,
      Model model) {
    model.addAttribute("bags", inventoryService.getInventoryFiltered(bloodType, status, sortExp));
    model.addAttribute("lowStock", inventoryService.getLowStockWarnings());
    return "admin/inventory";
  }
}