package com.login.AxleXpert.bookings.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Services.entity.Service;
import com.login.AxleXpert.Services.repository.ServiceRepository;
import com.login.AxleXpert.Tasks.dto.TaskDTO;
import com.login.AxleXpert.Tasks.service.TaskService;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.dto.BookingDTO;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.notifications.service.NotificationService;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * ============================================================================
 * BookingService Unit Tests - Comprehensive Test Suite
 * ============================================================================
 * 
 * LEARNING: Testing Business Logic
 * - BookingService handles the core booking workflow
 * - Tests cover creation, assignment, rejection, and retrieval
 * - Validates business rules (status transitions, employee roles, etc.)
 * 
 * Test Coverage:
 * - Creating bookings with valid/invalid data
 * - Assigning employees to bookings
 * - Rejecting bookings with reasons
 * - Retrieving bookings (all, by ID, limited count)
 * - Deleting bookings
 * - Status transitions and validations
 * - Edge cases and exceptions
 * 
 * Target Coverage: 95%+
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ServiceRepository serviceRepository;
    
    @Mock
    private BranchRepository branchRepository;
    
    @Mock
    private TaskService taskService;
    
    @Mock
    private NotificationService notificationService;
    
    private BookingService bookingService;
    
    // Test data
    private User testCustomer;
    private User testEmployee;
    private Service testService;
    private Branch testBranch;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        /**
         * LEARNING: BookingService uses constructor injection
         * All dependencies are final and injected via constructor
         * This is the preferred pattern in modern Spring
         */
        bookingService = new BookingService(
            bookingRepository,
            userRepository,
            taskService,
            branchRepository,
            serviceRepository,
            notificationService
        );
        
        // Create test data
        testCustomer = TestDataBuilder.createCustomer();
        testEmployee = TestDataBuilder.createEmployee();
        testService = TestDataBuilder.createService();
        testBranch = TestDataBuilder.createBranch();
        testBooking = TestDataBuilder.createBooking();
    }

    // ========================================================================
    // CREATE BOOKING TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Create Booking Tests")
    class CreateBookingTests {
        
        @Test
        @DisplayName("Should create booking with valid data")
        void shouldCreateBooking_whenValidDataProvided() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, // id
                testCustomer.getId(),
                "John Doe",
                "555-1234",
                "Toyota Camry",
                testBranch.getId(),
                null, // branchName
                testService.getId(),
                null, // serviceName
                LocalDateTime.now().plusDays(1).toString(),
                null, // endAt
                null, // status
                null, // assignedEmployeeId
                null, // assignedEmployeeName
                null, // totalPrice
                "Test notes",
                null, // createdAt
                null  // updatedAt
            );
            
            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            when(userRepository.findById(testCustomer.getId()))
                .thenReturn(Optional.of(testCustomer));
            when(bookingRepository.existsByBranch_IdAndStartAt(any(), any()))
                .thenReturn(false);
            when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(1L);
                    return booking;
                });
            
            // ACT
            BookingDTO result = bookingService.createBooking(dto);
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.customerId()).isEqualTo(testCustomer.getId());
            assertThat(result.status()).isEqualTo(BookingStatus.PENDING);
            
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw exception when booking data is null")
        void shouldThrowException_whenBookingDataIsNull() {
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking data is required");
            
            verify(bookingRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw exception when branchId is null")
        void shouldThrowException_whenBranchIdIsNull() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, testCustomer.getId(), "John", "555", "Car",
                null, null, testService.getId(), null, null, null,
                null, null, null, null, null, null, null
            );
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("branchId is required");
        }
        
        @Test
        @DisplayName("Should throw exception when serviceId is null")
        void shouldThrowException_whenServiceIdIsNull() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, testCustomer.getId(), "John", "555", "Car",
                testBranch.getId(), null, null, null, null, null,
                null, null, null, null, null, null, null
            );
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("serviceId is required");
        }
        
        @Test
        @DisplayName("Should throw exception when customerId is null")
        void shouldThrowException_whenCustomerIdIsNull() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, null, "John", "555", "Car",
                testBranch.getId(), null, testService.getId(), null, null, null,
                null, null, null, null, null, null, null
            );
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("customerId is required");
        }
        
        @Test
        @DisplayName("Should throw exception when branch not found")
        void shouldThrowException_whenBranchNotFound() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, testCustomer.getId(), "John", "555", "Car",
                999L, null, testService.getId(), null, null, null,
                null, null, null, null, null, null, null
            );
            
            when(branchRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Branch not found");
        }
        
        @Test
        @DisplayName("Should throw exception when service not found")
        void shouldThrowException_whenServiceNotFound() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, testCustomer.getId(), "John", "555", "Car",
                testBranch.getId(), null, 999L, null, null, null,
                null, null, null, null, null, null, null
            );
            
            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(serviceRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service not found");
        }
        
        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowException_whenCustomerNotFound() {
            // ARRANGE
            BookingDTO dto = new BookingDTO(
                null, 999L, "John", "555", "Car",
                testBranch.getId(), null, testService.getId(), null, null, null,
                null, null, null, null, null, null, null
            );
            
            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer not found");
        }
        
        @Test
        @DisplayName("Should throw exception when booking slot already taken")
        void shouldThrowException_whenBookingSlotTaken() {
            // ARRANGE
            LocalDateTime startTime = LocalDateTime.now().plusDays(1);
            BookingDTO dto = new BookingDTO(
                null, testCustomer.getId(), "John", "555", "Car",
                testBranch.getId(), null, testService.getId(), null,
                startTime.toString(), null, null, null, null, null, null, null, null
            );
            
            // Only mock what's actually called before the exception is thrown
            when(bookingRepository.existsByBranch_IdAndStartAt(eq(testBranch.getId()), any()))
                .thenReturn(true);
            
            // ACT & ASSERT
            assertThatThrownBy(() -> bookingService.createBooking(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booking slot already taken");
        }
    }

    // ========================================================================
    // ASSIGN EMPLOYEE TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Assign Employee Tests")
    class AssignEmployeeTests {
        
        @Test
        @DisplayName("Should assign employee to pending booking")
        void shouldAssignEmployee_whenBookingIsPending() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.PENDING);
            testEmployee.setRole("EMPLOYEE");
            
            // Create a mock TaskDTO to return from createTaskForBooking (TaskDTO is a record)
            TaskDTO mockTaskDTO = new TaskDTO(
                1L, testBooking.getId(), testService.getId(), testEmployee.getId(),
                testEmployee.getUsername(), "Test Car", "Test Task", "Description",
                null, null, 60, null, null, null, null, null, null
            );
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(bookingRepository.save(any(Booking.class)))
                .thenReturn(testBooking);
            when(taskService.createTaskForBooking(any(), any())).thenReturn(mockTaskDTO); // Fix: returns TaskDTO, not void
            when(notificationService.createAndSendNotification(any(), any(), any(), any()))
                .thenReturn(null); // Fix: returns NotificationDTO, not void (we don't care about the return value)
            
            // ACT
            Optional<BookingDTO> result = bookingService.assignEmployee(
                testBooking.getId(),
                testEmployee.getId()
            );
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
            assertThat(testBooking.getAssignedEmployee()).isEqualTo(testEmployee);
            
            verify(bookingRepository, times(1)).save(testBooking);
            verify(taskService, times(1)).createTaskForBooking(testBooking.getId(), testEmployee.getId());
            verify(notificationService, times(1))
                .createAndSendNotification(eq(testEmployee.getId()), anyString(), anyString(), eq("EMPLOYEE"));
        }
        
        @Test
        @DisplayName("Should return empty when booking not found")
        void shouldReturnEmpty_whenBookingNotFound() {
            // ARRANGE
            when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<BookingDTO> result = bookingService.assignEmployee(999L, testEmployee.getId());
            
            // ASSERT
            assertThat(result).isEmpty();
            verify(bookingRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw exception when booking is cancelled")
        void shouldThrowException_whenBookingIsCancelled() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.CANCELLED);
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.assignEmployee(testBooking.getId(), testEmployee.getId())
            )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot approve a booking that has been rejected/cancelled");
        }
        
        @Test
        @DisplayName("Should throw exception when booking is already approved")
        void shouldThrowException_whenBookingAlreadyApproved() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.APPROVED);
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.assignEmployee(testBooking.getId(), testEmployee.getId())
            )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booking is already approved");
        }
        
        @Test
        @DisplayName("Should throw exception when employee not found")
        void shouldThrowException_whenEmployeeNotFound() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.PENDING);
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.assignEmployee(testBooking.getId(), 999L)
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found");
        }
        
        @Test
        @DisplayName("Should throw exception when user is not an employee")
        void shouldThrowException_whenUserIsNotEmployee() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.PENDING);
            testCustomer.setRole("CUSTOMER");
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(testCustomer.getId()))
                .thenReturn(Optional.of(testCustomer));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.assignEmployee(testBooking.getId(), testCustomer.getId())
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is not an employee");
        }
    }

    // ========================================================================
    // REJECT BOOKING TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Reject Booking Tests")
    class RejectBookingTests {
        
        @Test
        @DisplayName("Should reject pending booking with reason")
        void shouldRejectBooking_whenBookingIsPending() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.PENDING);
            testBooking.setNotes("");
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class)))
                .thenReturn(testBooking);
            
            // ACT
            Optional<BookingDTO> result = bookingService.rejectBooking(
                testBooking.getId(),
                "Customer requested cancellation",
                "Will reschedule later"
            );
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
            assertThat(testBooking.getNotes())
                .contains("Rejection reason: Customer requested cancellation")
                .contains("Additional notes: Will reschedule later");
            
            verify(bookingRepository, times(1)).save(testBooking);
        }
        
        @Test
        @DisplayName("Should return empty when booking not found")
        void shouldReturnEmpty_whenBookingNotFound() {
            // ARRANGE
            when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<BookingDTO> result = bookingService.rejectBooking(999L, "reason", "notes");
            
            // ASSERT
            assertThat(result).isEmpty();
        }
        
        @Test
        @DisplayName("Should throw exception when booking is already approved")
        void shouldThrowException_whenBookingAlreadyApproved() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.APPROVED);
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.rejectBooking(testBooking.getId(), "reason", "notes")
            )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot reject a booking that has been approved");
        }
        
        @Test
        @DisplayName("Should throw exception when booking is already cancelled")
        void shouldThrowException_whenBookingAlreadyCancelled() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.CANCELLED);
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                bookingService.rejectBooking(testBooking.getId(), "reason", "notes")
            )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booking is already rejected/cancelled");
        }
        
        @Test
        @DisplayName("Should handle null reason and notes gracefully")
        void shouldHandleNullReasonAndNotes() {
            // ARRANGE
            testBooking.setStatus(BookingStatus.PENDING);
            testBooking.setNotes("Existing notes");
            
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class)))
                .thenReturn(testBooking);
            
            // ACT
            Optional<BookingDTO> result = bookingService.rejectBooking(
                testBooking.getId(), null, null
            );
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
            assertThat(testBooking.getNotes()).isEqualTo("Existing notes");
        }
    }

    // ========================================================================
    // GET BOOKING TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Get Booking Tests")
    class GetBookingTests {
        
        @Test
        @DisplayName("Should return all bookings")
        void shouldReturnAllBookings() {
            // ARRANGE
            Booking booking1 = TestDataBuilder.createBooking();
            Booking booking2 = TestDataBuilder.createBooking();
            booking2.setId(2L);
            
            when(bookingRepository.findAll())
                .thenReturn(Arrays.asList(booking1, booking2));
            
            // ACT
            List<BookingDTO> result = bookingService.getAllBookings();
            
            // ASSERT
            assertThat(result).hasSize(2);
            verify(bookingRepository, times(1)).findAll();
        }
        
        @Test
        @DisplayName("Should return empty list when no bookings exist")
        void shouldReturnEmptyList_whenNoBookingsExist() {
            // ARRANGE
            when(bookingRepository.findAll())
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<BookingDTO> result = bookingService.getAllBookings();
            
            // ASSERT
            assertThat(result).isEmpty();
        }
        
        @Test
        @DisplayName("Should return limited number of bookings")
        void shouldReturnLimitedBookings_whenCountProvided() {
            // ARRANGE
            List<Booking> bookings = Arrays.asList(
                TestDataBuilder.createBooking(),
                TestDataBuilder.createBooking(),
                TestDataBuilder.createBooking()
            );
            when(bookingRepository.findAll()).thenReturn(bookings);
            
            // ACT
            List<BookingDTO> result = bookingService.getAllBookings(2);
            
            // ASSERT
            assertThat(result).hasSize(2);
        }
        
        @Test
        @DisplayName("Should return booking by ID when found")
        void shouldReturnBooking_whenIdExists() {
            // ARRANGE
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            
            // ACT
            Optional<BookingDTO> result = bookingService.getBookingById(testBooking.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(testBooking.getId());
        }
        
        @Test
        @DisplayName("Should return empty when booking not found")
        void shouldReturnEmpty_whenBookingNotFound() {
            // ARRANGE
            when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<BookingDTO> result = bookingService.getBookingById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // DELETE BOOKING TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Delete Booking Tests")
    class DeleteBookingTests {
        
        @Test
        @DisplayName("Should delete booking when it exists")
        void shouldDeleteBooking_whenBookingExists() {
            // ARRANGE
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            doNothing().when(bookingRepository).delete(testBooking);
            
            // ACT
            boolean result = bookingService.deleteBooking(testBooking.getId());
            
            // ASSERT
            assertThat(result).isTrue();
            verify(bookingRepository, times(1)).delete(testBooking);
        }
        
        @Test
        @DisplayName("Should return false when booking not found")
        void shouldReturnFalse_whenBookingNotFound() {
            // ARRANGE
            when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT
            boolean result = bookingService.deleteBooking(999L);
            
            // ASSERT
            assertThat(result).isFalse();
            verify(bookingRepository, never()).delete(any());
        }
    }
}
