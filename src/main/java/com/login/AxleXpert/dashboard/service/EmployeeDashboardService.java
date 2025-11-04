package com.login.AxleXpert.dashboard.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.CurrentUserUtil;
import com.login.AxleXpert.common.enums.TaskStatus;
import com.login.AxleXpert.dashboard.dto.DetailItemDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeActivityDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeServiceTypeDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeStatsDTO;
import com.login.AxleXpert.dashboard.dto.EmployeeTaskDTO;
import com.login.AxleXpert.dashboard.dto.ProductivityDataDTO;
import com.login.AxleXpert.dashboard.dto.StatsItemDTO;

@Service
public class EmployeeDashboardService {

    private final CurrentUserUtil currentUserUtil;
    private final TaskRepository taskRepository;
    private final BookingRepository bookingRepository;

    public EmployeeDashboardService(CurrentUserUtil currentUserUtil,
                                   TaskRepository taskRepository,
                                   BookingRepository bookingRepository) {
        this.currentUserUtil = currentUserUtil;
        this.taskRepository = taskRepository;
        this.bookingRepository = bookingRepository;
    }

    public EmployeeStatsDTO getEmployeeStats() {
        User currentUser = currentUserUtil.getCurrentUser();

        // Vehicles in service stats
        long vehiclesInService = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
        long pendingVehicles = taskRepository.countByAssignedEmployeeIdAndStatus(currentUser.getId(), TaskStatus.NOT_STARTED);
        long completedToday = taskRepository.countCompletedTodayByEmployee(currentUser.getId(), LocalDateTime.now());

        StatsItemDTO vehiclesStats = new StatsItemDTO(
            String.valueOf(vehiclesInService + pendingVehicles),
            "+" + (vehiclesInService + pendingVehicles) + " this week", // Mock data
            Arrays.asList(
                new DetailItemDTO("In Service", String.valueOf(vehiclesInService)),
                new DetailItemDTO("Pending", String.valueOf(pendingVehicles)),
                new DetailItemDTO("Completed", String.valueOf(completedToday)),
                new DetailItemDTO("This Month", "38") // Mock data
            )
        );

        // Services stats
        StatsItemDTO servicesStats = new StatsItemDTO(
            String.valueOf(completedToday),
            completedToday + " completed",
            Arrays.asList(
                new DetailItemDTO("Oil Changes", "3"), // Mock data
                new DetailItemDTO("Brake Services", "2"), // Mock data
                new DetailItemDTO("Inspections", "2"), // Mock data
                new DetailItemDTO("Other", "1") // Mock data
            )
        );

        // Work hours stats
        StatsItemDTO workHoursStats = new StatsItemDTO(
            "7.5",
            "Today",
            Arrays.asList(
                new DetailItemDTO("Today", "7.5h"),
                new DetailItemDTO("This Week", "38h"), // Mock data
                new DetailItemDTO("Overtime", "3h"), // Mock data
                new DetailItemDTO("Efficiency", "94%") // Mock data
            )
        );

        // Completion rate stats
        StatsItemDTO completionRateStats = new StatsItemDTO(
            "94%",
            "+2% this week",
            Arrays.asList(
                new DetailItemDTO("This Week", "94%"),
                new DetailItemDTO("Last Week", "92%"), // Mock data
                new DetailItemDTO("This Month", "93%"), // Mock data
                new DetailItemDTO("Best Month", "97%") // Mock data
            )
        );

        // Upcoming tasks stats
        long upcomingTasks = taskRepository.countByAssignedEmployeeIdAndStatusIn(
            currentUser.getId(),
            Arrays.asList(TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS)
        );

        StatsItemDTO upcomingTasksStats = new StatsItemDTO(
            String.valueOf(upcomingTasks),
            "Next 2 hours",
            Arrays.asList(
                new DetailItemDTO("Next Hour", "2"), // Mock data
                new DetailItemDTO("Next 2 Hours", "4"), // Mock data
                new DetailItemDTO("Today", String.valueOf(upcomingTasks)),
                new DetailItemDTO("Tomorrow", "9") // Mock data
            )
        );

        return new EmployeeStatsDTO(vehiclesStats, servicesStats, workHoursStats,
                                   completionRateStats, upcomingTasksStats);
    }

    public List<EmployeeTaskDTO> getEmployeeTasks() {
        User currentUser = currentUserUtil.getCurrentUser();
        return taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
                .map(task -> {
                    String vehicle = task.getBooking() != null && task.getBooking().getVehicle() != null ?
                        task.getBooking().getVehicle() : "N/A";
                    String customer = task.getBooking() != null ?
                        task.getBooking().getCustomerName() : "N/A";
                    int progress = calculateTaskProgress(task);
                    String time = task.getSheduledTime() != null ?
                        task.getSheduledTime().toLocalTime().toString() : "9:00 AM"; // Default

                    return new EmployeeTaskDTO(
                        task.getId(),
                        vehicle,
                        task.getTitle(),
                        progress,
                        time,
                        customer,
                        "Normal" // Mock priority
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ProductivityDataDTO> getEmployeeProductivity() {
        // Mock hourly productivity data
        return Arrays.asList(
            new ProductivityDataDTO("9AM", 1, 85),
            new ProductivityDataDTO("10AM", 2, 92),
            new ProductivityDataDTO("11AM", 1, 88),
            new ProductivityDataDTO("12PM", 0, 0),
            new ProductivityDataDTO("1PM", 2, 95),
            new ProductivityDataDTO("2PM", 1, 90),
            new ProductivityDataDTO("3PM", 1, 87),
            new ProductivityDataDTO("4PM", 0, 0)
        );
    }

    public List<EmployeeServiceTypeDTO> getEmployeeServiceTypes() {
        // Mock service type distribution
        return Arrays.asList(
            new EmployeeServiceTypeDTO("Oil Changes", 35, "#10b981"),
            new EmployeeServiceTypeDTO("Brake Service", 25, "#3b82f6"),
            new EmployeeServiceTypeDTO("Inspections", 20, "#f59e0b"),
            new EmployeeServiceTypeDTO("Tire Services", 15, "#8b5cf6"),
            new EmployeeServiceTypeDTO("Other", 5, "#ef4444")
        );
    }

    public List<EmployeeActivityDTO> getEmployeeRecentActivity() {
        User currentUser = currentUserUtil.getCurrentUser();
        return taskRepository.findByAssignedEmployeeIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .limit(10)
                .map(task -> {
                    String action = "";
                    String type = "";
                    if (task.getStatus() == TaskStatus.COMPLETED) {
                        action = "Completed " + task.getTitle().toLowerCase() +
                                (task.getBooking() != null && task.getBooking().getVehicle() != null ?
                                 " for " + task.getBooking().getVehicle() : "");
                        type = "completed";
                    } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                        action = "Started " + task.getTitle().toLowerCase() +
                                (task.getBooking() != null && task.getBooking().getVehicle() != null ?
                                 " for " + task.getBooking().getVehicle() : "");
                        type = "started";
                    } else {
                        action = "Assigned " + task.getTitle().toLowerCase();
                        type = "assigned";
                    }

                    String time = "Recently"; // Mock time
                    if (task.getUpdatedAt() != null) {
                        LocalDateTime now = LocalDateTime.now();
                        long minutes = java.time.Duration.between(task.getUpdatedAt(), now).toMinutes();
                        if (minutes < 60) {
                            time = minutes + " minutes ago";
                        } else if (minutes < 1440) {
                            time = (minutes / 60) + " hours ago";
                        } else {
                            time = (minutes / 1440) + " days ago";
                        }
                    }

                    return new EmployeeActivityDTO(action, time, type);
                })
                .collect(Collectors.toList());
    }

    private int calculateTaskProgress(com.login.AxleXpert.Tasks.entity.Task task) {
        if (task.getStatus() == TaskStatus.COMPLETED) {
            return 100;
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            return 75; // Mock progress
        } else {
            return 25; // Mock progress for not started
        }
    }
}