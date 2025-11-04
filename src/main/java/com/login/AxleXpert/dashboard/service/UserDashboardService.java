package com.login.AxleXpert.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Tasks.entity.Task;
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

        // Get all bookings for the user
        List<Booking> allBookings = bookingRepository.findByCustomerId(currentUser.getId());

        LocalDateTime now = LocalDateTime.now();

        // Compute appointment counts
        long completedAppointments = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .count();
        long inProgressAppointments = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .count();
        long pendingAppointments = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .count();

        // Compute this week/month counts for appointments
        long pendingThisWeek = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING && b.getStartAt() != null && b.getStartAt().isAfter(now) && b.getStartAt().isBefore(now.plusWeeks(1)))
                .count();
        long pendingThisMonth = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING && b.getStartAt() != null && b.getStartAt().getMonth() == now.getMonth())
                .count();

        // Compute next dates
        String nextPending = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING && b.getStartAt() != null && b.getStartAt().isAfter(now))
                .min((b1, b2) -> b1.getStartAt().compareTo(b2.getStartAt()))
                .map(b -> b.getStartAt().toLocalDate().toString())
                .orElse("None");

        // Compute task counts for active tasks
        long inProgressTasks = taskRepository.countByCustomerIdAndStatus(currentUser.getId(), TaskStatus.IN_PROGRESS);
        long pendingTasks = taskRepository.countByCustomerIdAndStatus(currentUser.getId(), TaskStatus.NOT_STARTED);
        long completedTodayTasks = taskRepository.countCompletedTodayByCustomer(currentUser.getId(), now);
        long completedThisMonthTasks = taskRepository.countCompletedThisMonthByCustomer(currentUser.getId(), now);
        long totalCompletedTasks = taskRepository.countByCustomerIdAndStatus(currentUser.getId(), TaskStatus.COMPLETED);

        // Get completed tasks for service details
        List<Task> completedTasks = taskRepository.findByCustomerId(currentUser.getId()).stream()
            .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
            .collect(Collectors.toList());

        long oilChanges = completedTasks.stream()
            .filter(t -> t.getBooking() != null && t.getBooking().getService() != null && 
                        t.getBooking().getService().getName().toLowerCase().contains("oil"))
            .count();
        long brakeServices = completedTasks.stream()
            .filter(t -> t.getBooking() != null && t.getBooking().getService() != null && 
                        t.getBooking().getService().getName().toLowerCase().contains("brake"))
            .count();
        long inspections = completedTasks.stream()
            .filter(t -> t.getBooking() != null && t.getBooking().getService() != null && 
                        t.getBooking().getService().getName().toLowerCase().contains("inspection"))
            .count();
        long otherServices = totalCompletedTasks - oilChanges - brakeServices - inspections;

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
                new DetailItemDTO("Recently Serviced", String.valueOf(totalVehicles - vehiclesNeedingService))
            )
        );

        // Active tasks stats -> In-progress and pending tasks
        StatsItemDTO activeTasksStats = new StatsItemDTO(
            String.valueOf(inProgressTasks + pendingTasks),
            "+" + (inProgressTasks + pendingTasks) + " this week",
            Arrays.asList(
                new DetailItemDTO("In Progress", String.valueOf(inProgressTasks)),
                new DetailItemDTO("Pending", String.valueOf(pendingTasks)),
                new DetailItemDTO("Completed Today", String.valueOf(completedTodayTasks)),
                new DetailItemDTO("This Month", String.valueOf(completedThisMonthTasks))
            )
        );

        // Service history stats -> Completed tasks
        StatsItemDTO serviceHistoryStats = new StatsItemDTO(
            String.valueOf(totalCompletedTasks),
            "+" + completedThisMonthTasks + " this month",
            Arrays.asList(
                new DetailItemDTO("Oil Changes", String.valueOf(oilChanges)),
                new DetailItemDTO("Brake Services", String.valueOf(brakeServices)),
                new DetailItemDTO("Inspections", String.valueOf(inspections)),
                new DetailItemDTO("Other Services", String.valueOf(otherServices))
            )
        );

        // Appointments stats -> Pending and in-progress appointments
        StatsItemDTO appointmentsStats = new StatsItemDTO(
            String.valueOf(pendingAppointments + inProgressAppointments),
            "Next: " + nextPending,
            Arrays.asList(
                new DetailItemDTO("Pending", String.valueOf(pendingAppointments)),
                new DetailItemDTO("In Progress", String.valueOf(inProgressAppointments))
            )
        );


        return new UserStatsDTO(vehiclesStats, activeTasksStats, serviceHistoryStats,
                               appointmentsStats);
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

        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        List<Task> completedTasksInPeriod = taskRepository.findByCustomerId(currentUser.getId()).stream()
            .filter(t -> t.getStatus() == TaskStatus.COMPLETED && t.getCompletedTime() != null && t.getCompletedTime().isAfter(startDate))
            .collect(Collectors.toList());

        // Generate chart data for the last 'months' months
        LocalDate now = LocalDate.now();
        List<ChartDataDTO> chartData = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            long count = completedTasksInPeriod.stream()
                .filter(t -> {
                    LocalDate completedDate = t.getCompletedTime().toLocalDate();
                    return !completedDate.isBefore(monthStart) && !completedDate.isAfter(monthEnd);
                })
                .count();
            int revenue = (int) (count * 100); // Mock revenue calculation
            String monthName = monthStart.getMonth().name().substring(0, 3);
            chartData.add(new ChartDataDTO(monthName, (int) count, revenue));
        }

        // Generate breakdown data by service type
        Map<String, Long> serviceCounts = completedTasksInPeriod.stream()
            .collect(Collectors.groupingBy(
                t -> t.getBooking() != null && t.getBooking().getService() != null ? t.getBooking().getService().getName() : "Other Services",
                Collectors.counting()
            ));

        String[] colors = {"#10b981", "#3b82f6", "#f59e0b", "#8b5cf6", "#ef4444", "#8b5cf6"};
        List<BreakdownItemDTO> breakdown = new ArrayList<>();
        int colorIndex = 0;
        for (Map.Entry<String, Long> entry : serviceCounts.entrySet()) {
            String color = colors[colorIndex % colors.length];
            breakdown.add(new BreakdownItemDTO(entry.getKey(), entry.getValue().intValue(), color, entry.getValue().intValue()));
            colorIndex++;
        }

        return new ServiceHistoryDTO(chartData, breakdown);
    }

    public List<UserRecentTaskDTO> getUserRecentTasks() {
        User currentUser = currentUserUtil.getCurrentUser();
        return taskRepository.findByCustomerId(currentUser.getId()).stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(10)
                .map(task -> {
                    String vehicle = task.getBooking() != null && task.getBooking().getVehicle() != null ?
                        task.getBooking().getVehicle() : "N/A";
                    String date = task.getCompletedTime() != null ?
                        task.getCompletedTime().toLocalDate().toString() : "N/A";

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