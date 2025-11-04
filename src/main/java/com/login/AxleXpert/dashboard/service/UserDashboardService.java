package com.login.AxleXpert.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Vehicals.repository.VehicleRepository;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.CurrentUserUtil;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.common.enums.TaskStatus;
import com.login.AxleXpert.dashboard.dto.BreakdownItemDTO;
import com.login.AxleXpert.dashboard.dto.ChartDataDTO;
import com.login.AxleXpert.dashboard.dto.DetailItemDTO;
import com.login.AxleXpert.dashboard.dto.ServiceHistoryDTO;
import com.login.AxleXpert.dashboard.dto.StatsItemDTO;
import com.login.AxleXpert.dashboard.dto.UserAppointmentDTO;
import com.login.AxleXpert.dashboard.dto.UserRecentTaskDTO;
import com.login.AxleXpert.dashboard.dto.UserStatsDTO;
import com.login.AxleXpert.dashboard.dto.UserVehicleDTO;

@Service
public class UserDashboardService {

    private final CurrentUserUtil currentUserUtil;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final TaskRepository taskRepository;

    public UserDashboardService(CurrentUserUtil currentUserUtil,
                               VehicleRepository vehicleRepository,
                               BookingRepository bookingRepository,
                               TaskRepository taskRepository) {
        this.currentUserUtil = currentUserUtil;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.taskRepository = taskRepository;
    }

    public UserStatsDTO getUserStats() {
        User currentUser = currentUserUtil.getCurrentUser();

        // Vehicles stats
        List<UserVehicleDTO> vehicles = getUserVehicles();
        long totalVehicles = vehicles.size();
        long vehiclesNeedingService = vehicles.stream()
                .filter(v -> "due".equals(v.serviceStatus()))
                .count();

        StatsItemDTO vehiclesStats = new StatsItemDTO(
            String.valueOf(totalVehicles),
            vehiclesNeedingService + " needs service",
            Arrays.asList(
                new DetailItemDTO("Active Vehicles", String.valueOf(totalVehicles)),
                new DetailItemDTO("Service Due", String.valueOf(vehiclesNeedingService)),
                new DetailItemDTO("Recently Serviced", String.valueOf(totalVehicles - vehiclesNeedingService)),
                new DetailItemDTO("Total Visits", "24") // Mock data
            )
        );

        // Active tasks stats
        long activeTasks = taskRepository.countByAssignedEmployeeIdAndStatusIn(
            currentUser.getId(),
            Arrays.asList(TaskStatus.IN_PROGRESS, TaskStatus.NOT_STARTED)
        );
        long pendingTasks = taskRepository.countByAssignedEmployeeIdAndStatus(
            currentUser.getId(), TaskStatus.NOT_STARTED
        );
        long completedToday = taskRepository.countCompletedTodayByEmployee(currentUser.getId(), LocalDateTime.now());

        StatsItemDTO activeTasksStats = new StatsItemDTO(
            String.valueOf(activeTasks),
            "+2 this week", // Mock data
            Arrays.asList(
                new DetailItemDTO("In Progress", String.valueOf(activeTasks - pendingTasks)),
                new DetailItemDTO("Pending", String.valueOf(pendingTasks)),
                new DetailItemDTO("Completed Today", String.valueOf(completedToday)),
                new DetailItemDTO("This Month", "12") // Mock data
            )
        );

        // Service history stats
        long totalServices = bookingRepository.countByCustomerIdAndStatus(
            currentUser.getId(), BookingStatus.COMPLETED
        );

        StatsItemDTO serviceHistoryStats = new StatsItemDTO(
            String.valueOf(totalServices),
            "+3 this month", // Mock data
            Arrays.asList(
                new DetailItemDTO("Oil Changes", "8"), // Mock data
                new DetailItemDTO("Brake Services", "4"), // Mock data
                new DetailItemDTO("Inspections", "6"), // Mock data
                new DetailItemDTO("Other Services", "6") // Mock data
            )
        );

        // Appointments stats
        List<UserAppointmentDTO> appointments = getUserAppointments();
        long upcomingAppointments = appointments.stream()
                .filter(a -> !"completed".equals(a.status()))
                .count();
        long confirmedAppointments = appointments.stream()
                .filter(a -> "confirmed".equals(a.status()))
                .count();

        StatsItemDTO appointmentsStats = new StatsItemDTO(
            String.valueOf(upcomingAppointments),
            "Next: Tomorrow", // Mock data
            Arrays.asList(
                new DetailItemDTO("This Week", String.valueOf(upcomingAppointments)),
                new DetailItemDTO("This Month", "4"), // Mock data
                new DetailItemDTO("Confirmed", String.valueOf(confirmedAppointments)),
                new DetailItemDTO("Past Appointments", "28") // Mock data
            )
        );

        // Satisfaction stats
        StatsItemDTO satisfactionStats = new StatsItemDTO(
            "4.8", // Mock data
            "Excellent rating", // Mock data
            Arrays.asList(
                new DetailItemDTO("Average Rating", "4.8/5"), // Mock data
                new DetailItemDTO("Total Reviews", "18"), // Mock data
                new DetailItemDTO("5-Star Services", "15"), // Mock data
                new DetailItemDTO("Recommendations", "12") // Mock data
            )
        );

        return new UserStatsDTO(vehiclesStats, activeTasksStats, serviceHistoryStats,
                               appointmentsStats, satisfactionStats);
    }

    public List<UserVehicleDTO> getUserVehicles() {
        User currentUser = currentUserUtil.getCurrentUser();
        return vehicleRepository.findByUser_Id(currentUser.getId()).stream()
                .map(vehicle -> {
                    String serviceStatus = "good"; // Default
                    if (vehicle.getLastServiceDate() != null) {
                        LocalDate lastService = vehicle.getLastServiceDate();
                        LocalDate now = LocalDate.now();
                        if (lastService.isBefore(now.minusMonths(6))) {
                            serviceStatus = "due";
                        } else if (lastService.isAfter(now.minusMonths(1))) {
                            serviceStatus = "recent";
                        }
                    }

                    String lastService = vehicle.getLastServiceDate() != null ?
                        vehicle.getLastServiceDate().toString() : "N/A";

                    return new UserVehicleDTO(
                        vehicle.getId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getPlateNumber(),
                        serviceStatus,
                        lastService
                    );
                })
                .collect(Collectors.toList());
    }

    public List<UserAppointmentDTO> getUserAppointments() {
        User currentUser = currentUserUtil.getCurrentUser();
        return bookingRepository.findByCustomerId(currentUser.getId()).stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .map(booking -> {
                    String date = booking.getStartAt() != null ?
                        booking.getStartAt().toLocalDate().toString() : "TBD";
                    String time = booking.getStartAt() != null ?
                        booking.getStartAt().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a")) : "TBD";
                    String vehicle = booking.getVehicle() != null ? booking.getVehicle() : "N/A";
                    String status = booking.getStatus().toString().toLowerCase();

                    return new UserAppointmentDTO(
                        booking.getId(),
                        booking.getService().getName(),
                        date,
                        time,
                        vehicle,
                        status
                    );
                })
                .collect(Collectors.toList());
    }

    public ServiceHistoryDTO getUserServiceHistory(int months) {
        User currentUser = currentUserUtil.getCurrentUser();

        // Mock chart data - in real implementation, this would aggregate from bookings
        List<ChartDataDTO> chartData = Arrays.asList(
            new ChartDataDTO("Jan", 2, 150),
            new ChartDataDTO("Feb", 1, 80),
            new ChartDataDTO("Mar", 3, 220),
            new ChartDataDTO("Apr", 2, 180),
            new ChartDataDTO("May", 4, 350),
            new ChartDataDTO("Jun", 1, 90)
        );

        // Mock breakdown data
        List<BreakdownItemDTO> breakdown = Arrays.asList(
            new BreakdownItemDTO("Oil Changes", 8, "#10b981", 8),
            new BreakdownItemDTO("Brake Services", 4, "#3b82f6", 4),
            new BreakdownItemDTO("Inspections", 6, "#f59e0b", 6),
            new BreakdownItemDTO("Other Services", 6, "#8b5cf6", 6)
        );

        return new ServiceHistoryDTO(chartData, breakdown);
    }

    public List<UserRecentTaskDTO> getUserRecentTasks() {
        User currentUser = currentUserUtil.getCurrentUser();
        return taskRepository.findByAssignedEmployeeIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .limit(10)
                .map(task -> {
                    String vehicle = task.getBooking() != null && task.getBooking().getVehicle() != null ?
                        task.getBooking().getVehicle() : "N/A";
                    String date = task.getCreatedAt() != null ?
                        task.getCreatedAt().toLocalDate().toString() : "Today";

                    return new UserRecentTaskDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getStatus().toString().replace("_", " "),
                        "Normal", // Mock priority
                        vehicle,
                        date
                    );
                })
                .collect(Collectors.toList());
    }
}