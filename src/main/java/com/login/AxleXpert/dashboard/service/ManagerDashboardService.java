package com.login.AxleXpert.dashboard.service;

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

        // Revenue stats - mock data for now
        StatsItemDTO revenueStats = new StatsItemDTO(
            "$45.2K",
            "+12.5% this month",
            Arrays.asList(
                new DetailItemDTO("This Month", "$45.2K"),
                new DetailItemDTO("Last Month", "$40.1K"),
                new DetailItemDTO("Services", "$28.5K"),
                new DetailItemDTO("Parts", "$16.7K")
            )
        );

        // Users stats
        long totalUsers = userRepository.count();
        long employees = userRepository.findByRoleIgnoreCase("EMPLOYEE").size();
        long customers = totalUsers - employees;
        long newUsersThisWeek = 8; // Mock data

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
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .count();
        long pendingBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .count();
        long bookingsThisWeek = 89; // Mock data

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

        // Branches stats
        long totalBranches = branchRepository.count();

        StatsItemDTO branchesStats = new StatsItemDTO(
            String.valueOf(totalBranches),
            "All operational",
            Arrays.asList(
                new DetailItemDTO("Downtown", "Active"),
                new DetailItemDTO("Uptown", "Active"),
                new DetailItemDTO("Eastside", "Active"),
                new DetailItemDTO("Westside", "Active")
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
        // Mock data - in real implementation, this would aggregate from bookings/services
        return Arrays.asList(
            new RevenueDataDTO("Jan", 32000, 145, 89),
            new RevenueDataDTO("Feb", 28000, 132, 76),
            new RevenueDataDTO("Mar", 35000, 158, 95),
            new RevenueDataDTO("Apr", 31000, 142, 82),
            new RevenueDataDTO("May", 38000, 167, 98),
            new RevenueDataDTO("Jun", 29000, 128, 74)
        );
    }

    public List<BranchPerformanceDTO> getBranchPerformance() {
        return branchRepository.findAll().stream()
                .map(branch -> {
                    // Mock performance data - in real implementation, calculate from actual data
                    int services = 150 + (int)(Math.random() * 50); // Random between 150-200
                    int revenue = services * 150; // Mock revenue calculation
                    int efficiency = 85 + (int)(Math.random() * 15); // Random between 85-100
                    int employees = 5 + (int)(Math.random() * 5); // Random between 5-10

                    return new BranchPerformanceDTO(
                        branch.getName(),
                        services,
                        revenue,
                        efficiency,
                        employees
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ServiceDistributionDTO> getServiceDistribution() {
        // Mock data - in real implementation, aggregate from services/bookings
        return Arrays.asList(
            new ServiceDistributionDTO("Oil Changes", 35, "#10b981", 145),
            new ServiceDistributionDTO("Brake Services", 25, "#3b82f6", 104),
            new ServiceDistributionDTO("Inspections", 20, "#f59e0b", 83),
            new ServiceDistributionDTO("Tire Services", 15, "#8b5cf6", 62),
            new ServiceDistributionDTO("Other", 5, "#ef4444", 21)
        );
    }

    public List<RecentBookingDTO> getRecentBookings(int limit) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getCreatedAt() != null)
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