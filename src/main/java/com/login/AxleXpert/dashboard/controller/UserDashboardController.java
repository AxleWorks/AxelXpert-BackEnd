package com.login.AxleXpert.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.dashboard.dto.ServiceHistoryDTO;
import com.login.AxleXpert.dashboard.dto.UserAppointmentDTO;
import com.login.AxleXpert.dashboard.dto.UserRecentTaskDTO;
import com.login.AxleXpert.dashboard.dto.UserStatsDTO;
import com.login.AxleXpert.dashboard.dto.UserVehicleDTO;
import com.login.AxleXpert.dashboard.service.UserDashboardService;


@RestController
@RequestMapping("/api/dashboard/user")

public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    public UserDashboardController(UserDashboardService userDashboardService) {
        this.userDashboardService = userDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        UserStatsDTO stats = userDashboardService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<UserVehicleDTO>> getUserVehicles() {
        List<UserVehicleDTO> vehicles = userDashboardService.getUserVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<UserAppointmentDTO>> getUserAppointments() {
        List<UserAppointmentDTO> appointments = userDashboardService.getUserAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/service-history")
    public ResponseEntity<ServiceHistoryDTO> getUserServiceHistory(
            @RequestParam(defaultValue = "6") int months) {
        ServiceHistoryDTO history = userDashboardService.getUserServiceHistory(months);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/recent-tasks")
    public ResponseEntity<List<UserRecentTaskDTO>> getUserRecentTasks() {
        List<UserRecentTaskDTO> tasks = userDashboardService.getUserRecentTasks();
        return ResponseEntity.ok(tasks);
    }
}