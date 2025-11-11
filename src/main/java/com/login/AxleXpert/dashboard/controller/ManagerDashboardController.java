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
import com.login.AxleXpert.dashboard.service.ManagerDashboardService;

@RestController
@RequestMapping("/api/dashboard/manager")

public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;

    public ManagerDashboardController(ManagerDashboardService managerDashboardService) {
        this.managerDashboardService = managerDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ManagerStatsDTO> getManagerStats() {
        ManagerStatsDTO stats = managerDashboardService.getManagerStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueDataDTO>> getRevenueData(
            @RequestParam(defaultValue = "6") int months) {
        List<RevenueDataDTO> revenue = managerDashboardService.getRevenueData(months);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchPerformanceDTO>> getBranchPerformance() {
        List<BranchPerformanceDTO> branches = managerDashboardService.getBranchPerformance();
        return ResponseEntity.ok(branches);
    }

    @GetMapping("/service-distribution")
    public ResponseEntity<List<ServiceDistributionDTO>> getServiceDistribution() {
        List<ServiceDistributionDTO> distribution = managerDashboardService.getServiceDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/recent-bookings")
    public ResponseEntity<List<RecentBookingDTO>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit) {
        List<RecentBookingDTO> bookings = managerDashboardService.getRecentBookings(limit);
        return ResponseEntity.ok(bookings);
    }
}