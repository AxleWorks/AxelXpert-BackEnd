package com.login.AxleXpert.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.dashboard.dto.BranchPerformanceDTO;
import com.login.AxleXpert.dashboard.dto.ManagerStatsDTO;
import com.login.AxleXpert.dashboard.dto.RecentBookingDTO;
import com.login.AxleXpert.dashboard.dto.RevenueDataDTO;
import com.login.AxleXpert.dashboard.dto.ServiceDistributionDTO;
import com.login.AxleXpert.dashboard.service.AdminDashboardService;

@RestController
@RequestMapping("/api/dashboard/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ManagerStatsDTO> getAdminStats() {
        ManagerStatsDTO stats = adminDashboardService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueDataDTO>> getRevenueData(
            @RequestParam(defaultValue = "6") int months) {
        List<RevenueDataDTO> revenue = adminDashboardService.getRevenueData(months);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchPerformanceDTO>> getBranchPerformance() {
        List<BranchPerformanceDTO> branches = adminDashboardService.getBranchPerformance();
        return ResponseEntity.ok(branches);
    }

    @GetMapping("/service-distribution")
    public ResponseEntity<List<ServiceDistributionDTO>> getServiceDistribution() {
        List<ServiceDistributionDTO> distribution = adminDashboardService.getServiceDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/recent-bookings")
    public ResponseEntity<List<RecentBookingDTO>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit) {
        List<RecentBookingDTO> bookings = adminDashboardService.getRecentBookings(limit);
        return ResponseEntity.ok(bookings);
    }
}