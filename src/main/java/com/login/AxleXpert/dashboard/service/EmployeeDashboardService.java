package com.login.AxleXpert.dashboard.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Services.repository.ServiceRepository;
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
    private final ServiceRepository serviceRepository;

    public EmployeeDashboardService(CurrentUserUtil currentUserUtil,
                                   TaskRepository taskRepository,
                                   BookingRepository bookingRepository,
                                   ServiceRepository serviceRepository) {
        this.currentUserUtil = currentUserUtil;
        this.taskRepository = taskRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
    }

    public EmployeeStatsDTO getEmployeeStats() {
        User currentUser = currentUserUtil.getCurrentUser();

        // Vehicles in service stats - show IN_PROGRESS tasks
        long vehiclesInService = taskRepository.countByAssignedEmployeeIdAndStatus(currentUser.getId(), TaskStatus.IN_PROGRESS);
        long pendingVehicles = taskRepository.countByAssignedEmployeeIdAndStatus(currentUser.getId(), TaskStatus.NOT_STARTED);
        long completedToday = taskRepository.countCompletedTodayByEmployee(currentUser.getId(), LocalDateTime.now());
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long completedThisMonth = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                               task.getCompletedTime() != null && 
                               task.getCompletedTime().isAfter(startOfMonth))
                .count();

        StatsItemDTO vehiclesStats = new StatsItemDTO(
            String.valueOf(vehiclesInService),
            vehiclesInService + " in progress",
            Arrays.asList(
                new DetailItemDTO("In Progress", String.valueOf(vehiclesInService)),
                new DetailItemDTO("Pending", String.valueOf(pendingVehicles)),
                new DetailItemDTO("Completed Today", String.valueOf(completedToday)),
                new DetailItemDTO("This Month", String.valueOf(completedThisMonth))
            )
        );

        // Services stats - show total completed tasks this week
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        long completedThisWeek = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                               task.getCompletedTime() != null && 
                               task.getCompletedTime().isAfter(startOfWeek))
                .count();

        StatsItemDTO servicesStats = new StatsItemDTO(
            String.valueOf(completedThisWeek),
            completedThisWeek + " completed this week",
            Arrays.asList(
                new DetailItemDTO("Completed This Week", String.valueOf(completedThisWeek)),
                new DetailItemDTO("Completed Today", String.valueOf(completedToday)),
                new DetailItemDTO("Completed This Month", String.valueOf(completedThisMonth)),
                new DetailItemDTO("Pending", String.valueOf(pendingVehicles))
            )
        );

        // Work hours stats - sum of estimated duration for completed tasks, fallback to actual time spent
        List<com.login.AxleXpert.Tasks.entity.Task> completedTasks = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());
        
        double totalHoursToday = completedTasks.stream()
                .filter(task -> task.getCompletedTime() != null && 
                               task.getCompletedTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .mapToDouble(task -> calculateTaskHours(task))
                .sum();
        
        double totalHoursThisWeek = completedTasks.stream()
                .filter(task -> task.getCompletedTime() != null && 
                               task.getCompletedTime().isAfter(startOfWeek))
                .mapToDouble(task -> calculateTaskHours(task))
                .sum();
        
        double totalHoursThisMonth = completedTasks.stream()
                .filter(task -> task.getCompletedTime() != null && 
                               task.getCompletedTime().isAfter(startOfMonth))
                .mapToDouble(task -> calculateTaskHours(task))
                .sum();

        // Total hours worked (sum of all completed tasks)
        double totalHoursWorked = completedTasks.stream()
                .mapToDouble(task -> calculateTaskHours(task))
                .sum();

        StatsItemDTO workHoursStats = new StatsItemDTO(
            String.format("%.1f", totalHoursWorked),
            "Total hours worked",
            Arrays.asList(
                new DetailItemDTO("Today", String.format("%.1fh", totalHoursToday)),
                new DetailItemDTO("This Week", String.format("%.1fh", totalHoursThisWeek)),
                new DetailItemDTO("This Month", String.format("%.1fh", totalHoursThisMonth)),
                new DetailItemDTO("Completed Tasks", String.valueOf(completedTasks.size()))
            )
        );

        // Completion rate stats - percentage of completed tasks vs total tasks
        long totalTasks = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).size();
        long totalCompletedTasks = completedTasks.size();
        double completionRate = totalTasks > 0 ? (totalCompletedTasks * 100.0 / totalTasks) : 0.0;
        
        long tasksThisWeek = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getCreatedAt() != null && task.getCreatedAt().isAfter(startOfWeek))
                .count();
        long completedTasksThisWeek = completedThisWeek;
        double completionRateThisWeek = tasksThisWeek > 0 ? (completedTasksThisWeek * 100.0 / tasksThisWeek) : 0.0;
        
        long tasksThisMonth = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getCreatedAt() != null && task.getCreatedAt().isAfter(startOfMonth))
                .count();
        double completionRateThisMonth = tasksThisMonth > 0 ? (completedThisMonth * 100.0 / tasksThisMonth) : 0.0;

        StatsItemDTO completionRateStats = new StatsItemDTO(
            String.format("%.0f%%", completionRate),
            String.format("%.0f%% overall", completionRate),
            Arrays.asList(
                new DetailItemDTO("Overall", String.format("%.0f%%", completionRate)),
                new DetailItemDTO("This Week", String.format("%.0f%%", completionRateThisWeek)),
                new DetailItemDTO("This Month", String.format("%.0f%%", completionRateThisMonth)),
                new DetailItemDTO("Total Tasks", String.valueOf(totalTasks))
            )
        );

        // Upcoming tasks stats - show NOT_STARTED tasks
        long upcomingTasks = taskRepository.countByAssignedEmployeeIdAndStatus(currentUser.getId(), TaskStatus.NOT_STARTED);
        
        long upcomingTasksToday = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.NOT_STARTED && 
                               task.getSheduledTime() != null &&
                               task.getSheduledTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count();

        StatsItemDTO upcomingTasksStats = new StatsItemDTO(
            String.valueOf(upcomingTasks),
            "Not started",
            Arrays.asList(
                new DetailItemDTO("Not Started", String.valueOf(upcomingTasks)),
                new DetailItemDTO("Scheduled Today", String.valueOf(upcomingTasksToday)),
                new DetailItemDTO("In Progress", String.valueOf(vehiclesInService)),
                new DetailItemDTO("Total Pending", String.valueOf(upcomingTasks + vehiclesInService))
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
                    String time = "Not scheduled";
                    if (task.getSheduledTime() != null) {
                        time = task.getSheduledTime().toLocalTime().toString();
                    } else if (task.getStartTime() != null) {
                        time = task.getStartTime().toLocalTime().toString();
                    }

                    // Determine priority based on scheduled time and status
                    String priority = "Normal";
                    if (task.getSheduledTime() != null) {
                        LocalDateTime now = LocalDateTime.now();
                        long hoursUntil = java.time.Duration.between(now, task.getSheduledTime()).toHours();
                        if (hoursUntil < 0) {
                            priority = "High"; // Overdue
                        } else if (hoursUntil < 2) {
                            priority = "High"; // Due soon
                        } else if (hoursUntil < 6) {
                            priority = "Medium";
                        }
                    }

                    return new EmployeeTaskDTO(
                        task.getId(),
                        vehicle,
                        task.getTitle(),
                        progress,
                        time,
                        customer,
                        priority
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ProductivityDataDTO> getEmployeeProductivity() {
        User currentUser = currentUserUtil.getCurrentUser();
        
        // Get completed tasks for the last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<com.login.AxleXpert.Tasks.entity.Task> recentTasks = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                               task.getCompletedTime() != null &&
                               task.getCompletedTime().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());
        
        // Group tasks by date
        Map<java.time.LocalDate, List<com.login.AxleXpert.Tasks.entity.Task>> tasksByDate = recentTasks.stream()
                .collect(Collectors.groupingBy(task -> task.getCompletedTime().toLocalDate()));
        
        List<ProductivityDataDTO> productivity = new java.util.ArrayList<>();
        
        // Include all 7 days, even with 0 tasks
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            List<com.login.AxleXpert.Tasks.entity.Task> dateTasks = tasksByDate.getOrDefault(date, new java.util.ArrayList<>());
            
            int tasksCompleted = dateTasks.size();
            
            // Calculate efficiency as tasks per hour
            double totalHoursWorked = dateTasks.stream()
                    .mapToDouble(this::calculateTaskHours)
                    .sum();
            double efficiency = totalHoursWorked > 0 ? tasksCompleted / totalHoursWorked : 0;
            
            String dateLabel = date.equals(java.time.LocalDate.now()) ? "Today" : 
                              date.equals(java.time.LocalDate.now().minusDays(1)) ? "Yesterday" : 
                              date.getDayOfWeek().toString().substring(0, 3) + " " + date.getDayOfMonth();
            
            productivity.add(new ProductivityDataDTO(dateLabel, tasksCompleted, (int) efficiency));
        }
        
        return productivity;
    }

    public List<EmployeeServiceTypeDTO> getEmployeeServiceTypes() {
        User currentUser = currentUserUtil.getCurrentUser();
        
        // Get all completed tasks with their service information
        List<com.login.AxleXpert.Tasks.entity.Task> completedTasks = taskRepository.findByAssignedEmployeeIdWithBooking(currentUser.getId()).stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());
        
        if (completedTasks.isEmpty()) {
            return Arrays.asList();
        }
        
        // Group by task title and count
        Map<String, Long> taskCount = completedTasks.stream()
                .collect(Collectors.groupingBy(
                    com.login.AxleXpert.Tasks.entity.Task::getTitle,
                    Collectors.counting()
                ));
        
        long totalCount = completedTasks.size();
        
        // Define colors for services
        String[] colors = {"#10b981", "#3b82f6", "#f59e0b", "#8b5cf6", "#ef4444", "#06b6d4", "#ec4899", "#14b8a6"};
        
        // Create DTOs with task names and percentages
        List<EmployeeServiceTypeDTO> result = new java.util.ArrayList<>();
        int colorIndex = 0;
        
        for (Map.Entry<String, Long> entry : taskCount.entrySet()) {
            String taskName = entry.getKey();
            // Remove "Service Task - " prefix if present
            if (taskName.startsWith("Service Task - ")) {
                taskName = taskName.substring("Service Task - ".length());
            }
            int percentage = (int) ((entry.getValue() * 100) / totalCount);
            String color = colors[colorIndex % colors.length];
            
            result.add(new EmployeeServiceTypeDTO(taskName, percentage, color));
            colorIndex++;
        }
        
        // Sort by value descending
        result.sort((a, b) -> Integer.compare(b.value(), a.value()));
        
        return result;
    }

    public List<EmployeeActivityDTO> getEmployeeRecentActivity() {
        User currentUser = currentUserUtil.getCurrentUser();
        return taskRepository.findByAssignedEmployeeIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .limit(10)
                .map(task -> {
                    String action;
                    String type;
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

                    String time = "Recently";
                    if (task.getUpdatedAt() != null) {
                        LocalDateTime now = LocalDateTime.now();
                        long minutes = java.time.Duration.between(task.getUpdatedAt(), now).toMinutes();
                        if (minutes < 1) {
                            time = "Just now";
                        } else if (minutes < 60) {
                            time = minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
                        } else if (minutes < 1440) {
                            long hours = minutes / 60;
                            time = hours + " hour" + (hours > 1 ? "s" : "") + " ago";
                        } else {
                            long days = minutes / 1440;
                            time = days + " day" + (days > 1 ? "s" : "") + " ago";
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
            // Calculate based on subtasks if available
            if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
                long completedSubTasks = task.getSubTasks().stream()
                        .filter(st -> st.getStatus() == TaskStatus.COMPLETED)
                        .count();
                return (int) ((completedSubTasks * 100) / task.getSubTasks().size());
            }
            // If no subtasks, estimate based on time elapsed
            if (task.getStartTime() != null && task.getEstimatedTimeMinutes() != null) {
                long elapsedMinutes = java.time.Duration.between(task.getStartTime(), LocalDateTime.now()).toMinutes();
                int progress = (int) Math.min(95, (elapsedMinutes * 100) / task.getEstimatedTimeMinutes());
                return Math.max(10, progress); // At least 10% if in progress
            }
            return 50; // Default progress for in-progress tasks
        } else {
            return 0; // Not started
        }
    }

    private double calculateTaskHours(com.login.AxleXpert.Tasks.entity.Task task) {
        // First try to use estimated time if available
        if (task.getEstimatedTimeMinutes() != null && task.getEstimatedTimeMinutes() > 0) {
            return task.getEstimatedTimeMinutes() / 60.0;
        }
        
        // Fallback to actual time spent if start and completion times are available
        if (task.getStartTime() != null && task.getCompletedTime() != null) {
            long actualMinutes = java.time.Duration.between(task.getStartTime(), task.getCompletedTime()).toMinutes();
            return Math.max(0.1, actualMinutes / 60.0); // At least 0.1 hours (6 minutes) to avoid 0
        }
        
        // If no time data available, use a default of 1 hour
        return 1.0;
    }
}