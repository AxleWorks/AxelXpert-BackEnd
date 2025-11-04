package com.login.AxleXpert.dashboard.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.CurrentUserUtil;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.dashboard.dto.BranchPerformanceDTO;
import com.login.AxleXpert.dashboard.dto.DetailItemDTO;
import com.login.AxleXpert.dashboard.dto.ManagerStatsDTO;
import com.login.AxleXpert.dashboard.dto.RecentBookingDTO;
import com.login.AxleXpert.dashboard.dto.RevenueDataDTO;
import com.login.AxleXpert.dashboard.dto.ServiceDistributionDTO;
import com.login.AxleXpert.dashboard.dto.StatsItemDTO;
import com.login.AxleXpert.Branches.entity.Branch;

@Service
public class ManagerDashboardService {

    private final CurrentUserUtil currentUserUtil;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BranchRepository branchRepository;
    private final TaskRepository taskRepository;

    public ManagerDashboardService(CurrentUserUtil currentUserUtil,
                                  UserRepository userRepository,
                                  BookingRepository bookingRepository,
                                  BranchRepository branchRepository,
                                  TaskRepository taskRepository) {
        this.currentUserUtil = currentUserUtil;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.branchRepository = branchRepository;
        this.taskRepository = taskRepository;
    }

    public ManagerStatsDTO getManagerStats() {
        Long branchId = currentUserUtil.getCurrentUserBranchId();

        // Revenue stats
        double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getStatus() == BookingStatus.APPROVED)
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                .sum();
        StatsItemDTO revenueStats = new StatsItemDTO(
            "$" + String.format("%.1fK", totalRevenue / 1000),
            "+12.5% this month",
            Arrays.asList(
                new DetailItemDTO("This Month", "$" + String.format("%.1fK", totalRevenue / 1000)),
                new DetailItemDTO("Last Month", "$40.1K"),
                new DetailItemDTO("Services", "$28.5K"),
                new DetailItemDTO("Parts", "$16.7K")
            )
        );

        // Users stats
        long totalUsers = userRepository.findAll().stream()
                .filter(u -> u.getBranch() != null && u.getBranch().getId().equals(branchId))
                .count();
        long employees = userRepository.findAll().stream()
                .filter(u -> "EMPLOYEE".equalsIgnoreCase(u.getRole()) && u.getBranch() != null && u.getBranch().getId().equals(branchId))
                .count();
        long customers = totalUsers - employees;
        long newUsersThisWeek = userRepository.findAll().stream()
                .filter(u -> u.getBranch() != null && u.getBranch().getId().equals(branchId) && u.getCreatedAt() != null && u.getCreatedAt().isAfter(LocalDateTime.now().minusWeeks(1)))
                .count();

        StatsItemDTO usersStats = new StatsItemDTO(
            String.valueOf(totalUsers),
            "+" + newUsersThisWeek + " this week",
            Arrays.asList(
                new DetailItemDTO("Employees", String.valueOf(employees)),
                new DetailItemDTO("Customers", String.valueOf(customers)),
                new DetailItemDTO("New This Week", String.valueOf(newUsersThisWeek)),
                new DetailItemDTO("Active Today", "45") // Mock data
            )
        );

        // Bookings stats
        long totalBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId))
                .count();
        long confirmedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED && b.getBranch() != null && b.getBranch().getId().equals(branchId))
                .count();
        long pendingBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING && b.getBranch() != null && b.getBranch().getId().equals(branchId))
                .count();
        long bookingsThisWeek = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getCreatedAt() != null && b.getCreatedAt().isAfter(LocalDateTime.now().minusWeeks(1)))
                .count();

        StatsItemDTO bookingsStats = new StatsItemDTO(
            String.valueOf(bookingsThisWeek),
            "+15 today",
            Arrays.asList(
                new DetailItemDTO("Today", "15"), // Mock data
                new DetailItemDTO("This Week", String.valueOf(bookingsThisWeek)),
                new DetailItemDTO("Confirmed", String.valueOf(confirmedBookings)),
                new DetailItemDTO("Pending", String.valueOf(pendingBookings))
            )
        );

        // Branches stats - only current branch
        Branch currentBranch = branchRepository.findById(branchId).orElse(null);
        StatsItemDTO branchesStats = new StatsItemDTO(
            "1",
            "Operational",
            Arrays.asList(
                new DetailItemDTO(currentBranch != null ? currentBranch.getName() : "N/A", "Active")
            )
        );

        // Performance stats - mock data
        StatsItemDTO performanceStats = new StatsItemDTO(
            "94%",
            "+2% this month",
            Arrays.asList(
                new DetailItemDTO("Customer Satisfaction", "94%"),
                new DetailItemDTO("Service Quality", "92%"),
                new DetailItemDTO("Response Time", "96%"),
                new DetailItemDTO("Overall Rating", "4.7/5")
            )
        );

        return new ManagerStatsDTO(revenueStats, usersStats, bookingsStats,
                                  branchesStats, performanceStats);
    }

    public List<RevenueDataDTO> getRevenueData(int months) {
        Long branchId = currentUserUtil.getCurrentUserBranchId();
        List<RevenueDataDTO> data = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1);
            double revenue = bookingRepository.findAll().stream()
                    .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getCreatedAt() != null && b.getCreatedAt().isAfter(start) && b.getCreatedAt().isBefore(end) && b.getStatus() == BookingStatus.APPROVED)
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                    .sum();
            long bookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getCreatedAt() != null && b.getCreatedAt().isAfter(start) && b.getCreatedAt().isBefore(end))
                    .count();
            long services = bookings; // Assuming one service per booking
            String monthName = start.getMonth().name().substring(0, 3);
            data.add(new RevenueDataDTO(monthName, (int) revenue, (int) bookings, (int) services));
        }
        return data;
    }

    public List<BranchPerformanceDTO> getBranchPerformance() {
        Long branchId = currentUserUtil.getCurrentUserBranchId();
        Branch branch = branchRepository.findById(branchId).orElse(null);
        if (branch == null) return new ArrayList<>();
        long services = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId))
                .count();
        double revenue = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getStatus() == BookingStatus.APPROVED)
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                .sum();
        long confirmed = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId) && b.getStatus() == BookingStatus.APPROVED)
                .count();
        int efficiency = services > 0 ? (int) ((double) confirmed / services * 100) : 0;
        long employees = userRepository.findAll().stream()
                .filter(u -> "EMPLOYEE".equalsIgnoreCase(u.getRole()) && u.getBranch() != null && u.getBranch().getId().equals(branchId))
                .count();
        return List.of(new BranchPerformanceDTO(
            branch.getName(),
            (int) services,
            (int) revenue,
            efficiency,
            (int) employees
        ));
    }

    public List<ServiceDistributionDTO> getServiceDistribution() {
        Long branchId = currentUserUtil.getCurrentUserBranchId();
        var serviceCount = bookingRepository.findAll().stream()
                .filter(b -> b.getBranch() != null && b.getBranch().getId().equals(branchId))
                .collect(Collectors.groupingBy(b -> b.getService().getName(), Collectors.summingInt(b -> 1)));
        int total = serviceCount.values().stream().mapToInt(Integer::intValue).sum();
        List<String> colors = Arrays.asList("#10b981", "#3b82f6", "#f59e0b", "#8b5cf6", "#ef4444");
        int[] index = {0};
        return serviceCount.entrySet().stream()
                .map(e -> new ServiceDistributionDTO(
                    e.getKey(),
                    e.getValue(),
                    colors.get(index[0]++ % colors.size()),
                    total > 0 ? (int) Math.round((double) e.getValue() / total * 100.0) : 0
                ))
                .collect(Collectors.toList());
    }

    public List<RecentBookingDTO> getRecentBookings(int limit) {
        Long branchId = currentUserUtil.getCurrentUserBranchId();
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getCreatedAt() != null && booking.getBranch() != null && booking.getBranch().getId().equals(branchId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(booking -> new RecentBookingDTO(
                    booking.getId(),
                    booking.getCustomerName(),
                    booking.getService().getName(),
                    booking.getBranch() != null ? booking.getBranch().getName() : "N/A",
                    booking.getStatus().toString(),
                    booking.getCreatedAt().toLocalDate().toString(),
                    booking.getTotalPrice() != null ? "$" + booking.getTotalPrice().toString() : "N/A"
                ))
                .collect(Collectors.toList());
    }
}

