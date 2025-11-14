package com.login.AxleXpert.dashboard.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.dashboard.dto.ManagerStatsDTO;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for AdminDashboardService
 * 
 * Learning Focus:
 * - Testing complex aggregation logic
 * - Testing date/time filtering
 * - Testing statistical calculations
 * - Mocking multiple repository queries
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardService Unit Tests")
class AdminDashboardServiceTest {

    private AdminDashboardService adminDashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private TaskRepository taskRepository;

    private List<Booking> testBookings;
    private List<User> testUsers;
    private List<Branch> testBranches;

    @BeforeEach
    void setUp() {
        adminDashboardService = new AdminDashboardService(
            userRepository,
            bookingRepository,
            branchRepository,
            taskRepository
        );

        // Create test data
        testBranches = createTestBranches();
        testUsers = createTestUsers();
        testBookings = createTestBookings();
    }

    private List<Branch> createTestBranches() {
        Branch branch1 = TestDataBuilder.createBranch();
        branch1.setId(1L);
        branch1.setName("Main Branch");

        Branch branch2 = TestDataBuilder.createBranch();
        branch2.setId(2L);
        branch2.setName("Downtown Branch");

        return Arrays.asList(branch1, branch2);
    }

    private List<User> createTestUsers() {
        User customer1 = TestDataBuilder.createUser();
        customer1.setId(1L);
        customer1.setRole("CUSTOMER");
        customer1.setCreatedAt(LocalDateTime.now().minusDays(5)); // This week

        User customer2 = TestDataBuilder.createUser();
        customer2.setId(2L);
        customer2.setRole("CUSTOMER");
        customer2.setCreatedAt(LocalDateTime.now().minusMonths(1));

        User employee = TestDataBuilder.createUser();
        employee.setId(3L);
        employee.setRole("EMPLOYEE");
        employee.setCreatedAt(LocalDateTime.now().minusMonths(2));

        return Arrays.asList(customer1, customer2, employee);
    }

    private List<Booking> createTestBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lastMonth = thisMonth.minusMonths(1);

        // This month's approved booking
        Booking booking1 = TestDataBuilder.createBooking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setTotalPrice(new BigDecimal("5000.00"));
        booking1.setCreatedAt(now.minusDays(10));
        booking1.setCustomerName("Customer A");

        // Last month's approved booking
        Booking booking2 = TestDataBuilder.createBooking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setTotalPrice(new BigDecimal("4000.00"));
        booking2.setCreatedAt(lastMonth.plusDays(15));
        booking2.setCustomerName("Customer B");

        // This week's pending booking
        Booking booking3 = TestDataBuilder.createBooking();
        booking3.setId(3L);
        booking3.setStatus(BookingStatus.PENDING);
        booking3.setTotalPrice(new BigDecimal("2000.00"));
        booking3.setCreatedAt(now.minusDays(3));
        booking3.setCustomerName("Customer C");

        // Today's booking
        Booking booking4 = TestDataBuilder.createBooking();
        booking4.setId(4L);
        booking4.setStatus(BookingStatus.PENDING);
        booking4.setTotalPrice(new BigDecimal("1500.00"));
        booking4.setCreatedAt(now);
        booking4.setCustomerName("Customer D");

        return Arrays.asList(booking1, booking2, booking3, booking4);
    }

    @Nested
    @DisplayName("Admin Stats Tests")
    class AdminStatsTests {

        @Test
        @DisplayName("Should calculate admin stats with data")
        void shouldCalculateAdminStats_withData() {
            // ARRANGE
            when(bookingRepository.findAll()).thenReturn(testBookings);
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE"))
                .thenReturn(testUsers.stream()
                    .filter(u -> "EMPLOYEE".equals(u.getRole()))
                    .toList());
            when(bookingRepository.count()).thenReturn((long) testBookings.size());
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.revenue()).isNotNull();
            assertThat(result.users()).isNotNull();
            assertThat(result.bookings()).isNotNull();
            assertThat(result.branches()).isNotNull();
            assertThat(result.performance()).isNotNull();
            
            // Verify repository calls
            verify(bookingRepository, atLeastOnce()).findAll();
            verify(userRepository, atLeastOnce()).findAll();
            verify(branchRepository, atLeastOnce()).findAll();
        }

        @Test
        @DisplayName("Should handle empty bookings")
        void shouldHandleEmptyBookings() {
            // ARRANGE
            when(bookingRepository.findAll()).thenReturn(Collections.emptyList());
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn(0L);
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.revenue()).isNotNull();
            assertThat(result.bookings().value()).isEqualTo("0");
        }

        @Test
        @DisplayName("Should handle empty users")
        void shouldHandleEmptyUsers() {
            // ARRANGE
            when(bookingRepository.findAll()).thenReturn(testBookings);
            when(userRepository.findAll()).thenReturn(Collections.emptyList());
            when(userRepository.count()).thenReturn(0L);
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn((long) testBookings.size());
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.users().value()).isEqualTo("0");
        }

        @Test
        @DisplayName("Should calculate revenue correctly")
        void shouldCalculateRevenue() {
            // ARRANGE
            when(bookingRepository.findAll()).thenReturn(testBookings);
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn((long) testBookings.size());
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result.revenue()).isNotNull();
            assertThat(result.revenue().value()).isNotEmpty();
            assertThat(result.revenue().details()).isNotEmpty();
        }

        @Test
        @DisplayName("Should count branches correctly")
        void shouldCountBranches() {
            // ARRANGE
            when(bookingRepository.findAll()).thenReturn(testBookings);
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn((long) testBookings.size());
            when(branchRepository.count()).thenReturn(2L);
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result.branches()).isNotNull();
            assertThat(result.branches().value()).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null booking prices")
        void shouldHandleNullPrices() {
            // ARRANGE
            Booking bookingWithNullPrice = TestDataBuilder.createBooking();
            bookingWithNullPrice.setStatus(BookingStatus.APPROVED);
            bookingWithNullPrice.setTotalPrice(null);
            bookingWithNullPrice.setCreatedAt(LocalDateTime.now());

            when(bookingRepository.findAll()).thenReturn(Arrays.asList(bookingWithNullPrice));
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn(1L);
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT & ASSERT - Should not throw exception
            assertThatCode(() -> adminDashboardService.getAdminStats())
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle null created dates")
        void shouldHandleNullCreatedDates() {
            // ARRANGE
            Booking bookingWithNullDate = TestDataBuilder.createBooking();
            bookingWithNullDate.setCreatedAt(null);

            when(bookingRepository.findAll()).thenReturn(Arrays.asList(bookingWithNullDate));
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn(1L);
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT & ASSERT - Should not throw exception
            assertThatCode(() -> adminDashboardService.getAdminStats())
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle all bookings with same status")
        void shouldHandleAllSameStatus() {
            // ARRANGE
            List<Booking> allApproved = testBookings.stream()
                .peek(b -> b.setStatus(BookingStatus.APPROVED))
                .toList();

            when(bookingRepository.findAll()).thenReturn(allApproved);
            when(userRepository.findAll()).thenReturn(testUsers);
            when(userRepository.count()).thenReturn((long) testUsers.size());
            when(userRepository.findByRoleIgnoreCase("EMPLOYEE")).thenReturn(Collections.emptyList());
            when(bookingRepository.count()).thenReturn((long) allApproved.size());
            when(branchRepository.count()).thenReturn((long) testBranches.size());
            when(branchRepository.findAll()).thenReturn(testBranches);
            
            // ACT
            ManagerStatsDTO result = adminDashboardService.getAdminStats();
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.bookings()).isNotNull();
        }
    }
}
