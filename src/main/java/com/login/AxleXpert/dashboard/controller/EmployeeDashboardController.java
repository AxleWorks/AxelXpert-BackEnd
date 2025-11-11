package com.login.AxleXpert.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.dashboard.dto.EmployeeActivityDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeServiceTypeDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeStatsDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeTaskDTO;
import com.login.AxleXpert.dashboard.dto.ProductivityDataDTO;
import com.login.AxleXpert.dashboard.service.EmployeeDashboardService;

@RestController
@RequestMapping("/api/dashboard/employee")

public class EmployeeDashboardController {

    private final EmployeeDashboardService employeeDashboardService;

    public EmployeeDashboardController(EmployeeDashboardService employeeDashboardService) {
        this.employeeDashboardService = employeeDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<EmployeeStatsDTO> getEmployeeStats() {
        EmployeeStatsDTO stats = employeeDashboardService.getEmployeeStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<EmployeeTaskDTO>> getEmployeeTasks() {
        List<EmployeeTaskDTO> tasks = employeeDashboardService.getEmployeeTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/productivity")
    public ResponseEntity<List<ProductivityDataDTO>> getEmployeeProductivity() {
        List<ProductivityDataDTO> productivity = employeeDashboardService.getEmployeeProductivity();
        return ResponseEntity.ok(productivity);
    }

    @GetMapping("/service-types")
    public ResponseEntity<List<EmployeeServiceTypeDTO>> getEmployeeServiceTypes() {
        List<EmployeeServiceTypeDTO> serviceTypes = employeeDashboardService.getEmployeeServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<EmployeeActivityDTO>> getEmployeeRecentActivity() {
        List<EmployeeActivityDTO> activity = employeeDashboardService.getEmployeeRecentActivity();
        return ResponseEntity.ok(activity);
    }
}