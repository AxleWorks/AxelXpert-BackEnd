package com.login.AxleXpert.Users.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.dto.ProfileImageUpdateDTO;
import com.login.AxleXpert.Users.dto.UserDTO;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.EmailService;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for UserService
 * 
 * Learning Focus:
 * - Testing CRUD operations
 * - Testing profile update operations
 * - UserDTO is a Lombok @Data class (has getters/setters, not a record!)
 * - Using test data builders for consistent test setup
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmailService emailService;

    private User testEmployee;
    private User testCustomer;
    private User testManager;
    private User testAdmin;
    private Branch testBranch;

    @BeforeEach
    void setUp() {
        // Create service using constructor injection with all 5 dependencies
        userService = new UserService(
            userRepository,
            branchRepository,
            bookingRepository,
            taskRepository,
            emailService
        );

        // Create test data
        testEmployee = TestDataBuilder.createEmployee();
        testCustomer = TestDataBuilder.createCustomer();
        testManager = TestDataBuilder.createManager();
        testAdmin = TestDataBuilder.createUser();
        testAdmin.setRole("ADMIN");
        testBranch = TestDataBuilder.createBranch();
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user when user exists")
        void shouldReturnUser_whenUserExists() {
            // ARRANGE
            // UserService uses findByIdWithBranch() not findById()
            when(userRepository.findByIdWithBranch(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            
            // ACT
            Optional<UserDTO> result = userService.getUserById(testEmployee.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            // UserDTO is @Data Lombok class - use getUsername() not username()
            assertThat(result.get().getUsername()).isEqualTo(testEmployee.getUsername());
            verify(userRepository, times(1)).findByIdWithBranch(testEmployee.getId());
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmpty_whenUserNotFound() {
            // ARRANGE - No stubbing needed, service returns Optional.empty() when not found
            
            // ACT
            Optional<UserDTO> result = userService.getUserById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            // ARRANGE
            when(userRepository.findAll())
                .thenReturn(Arrays.asList(testEmployee, testCustomer, testManager));
            
            // ACT
            List<UserDTO> result = userService.getAllUsers();
            
            // ASSERT
            assertThat(result).hasSize(3);
            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUser_successfully() {
            // ARRANGE
            // UserDTO is a Lombok @Data class - use setters
            UserDTO updateDTO = new UserDTO();
            updateDTO.setId(testEmployee.getId());
            updateDTO.setUsername("updatedUser");
            updateDTO.setEmail(testEmployee.getEmail());
            updateDTO.setRole(testEmployee.getRole());
            updateDTO.setPhoneNumber("555-1234");
            updateDTO.setAddress("123 New St");

            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(userRepository.save(any(User.class)))
                .thenReturn(testEmployee);
            
            // ACT
            Optional<UserDTO> result = userService.updateUser(testEmployee.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(userRepository, times(1)).save(testEmployee);
        }

        @Test
        @DisplayName("Should return empty when updating non-existent user")
        void shouldReturnEmpty_whenUserNotFound() {
            // ARRANGE
            UserDTO updateDTO = new UserDTO();
            updateDTO.setId(999L);
            updateDTO.setUsername("test");

            when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<UserDTO> result = userService.updateUser(999L, updateDTO);
            
            // ASSERT
            assertThat(result).isEmpty();
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update profile image successfully")
        void shouldUpdateProfileImage_successfully() {
            // ARRANGE
            ProfileImageUpdateDTO imageDTO = new ProfileImageUpdateDTO(
                "https://example.com/image.jpg",
                "public123"
            );

            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(userRepository.save(any(User.class)))
                .thenReturn(testEmployee);
            
            // ACT
            Optional<UserDTO> result = userService.updateProfileImage(testEmployee.getId(), imageDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(userRepository, times(1)).save(testEmployee);
        }

        @Test
        @DisplayName("Should delete profile image successfully")
        void shouldDeleteProfileImage_successfully() {
            // ARRANGE
            testEmployee.setProfileImageUrl("https://example.com/old.jpg");

            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(userRepository.save(any(User.class)))
                .thenReturn(testEmployee);
            
            // ACT
            Optional<UserDTO> result = userService.deleteProfileImage(testEmployee.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            verify(userRepository, times(1)).save(testEmployee);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully when no dependencies")
        void shouldDeleteUser_successfully() {
            // ARRANGE
            when(userRepository.findById(testCustomer.getId()))
                .thenReturn(Optional.of(testCustomer));
            when(bookingRepository.existsByCustomerId(testCustomer.getId()))
                .thenReturn(false);
            when(bookingRepository.existsByAssignedEmployeeId(testCustomer.getId()))
                .thenReturn(false);
            when(taskRepository.existsByAssignedEmployeeId(testCustomer.getId()))
                .thenReturn(false);
            doNothing().when(userRepository).delete(testCustomer);
            
            // ACT
            boolean result = userService.deleteUser(testCustomer.getId());
            
            // ASSERT
            assertThat(result).isTrue();
            verify(userRepository, times(1)).delete(testCustomer);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowException_whenUserNotFound() {
            // ARRANGE
            when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should return false when user has bookings")
        void shouldReturnFalse_whenUserHasBookings() {
            // ARRANGE
            when(userRepository.findById(testCustomer.getId()))
                .thenReturn(Optional.of(testCustomer));
            when(bookingRepository.existsByCustomerId(testCustomer.getId()))
                .thenReturn(true);  // Has bookings as customer
            
            // ACT
            boolean result = userService.deleteUser(testCustomer.getId());
            
            // ASSERT
            assertThat(result).isFalse();
            verify(userRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty list when no users exist")
        void shouldHandleEmptyList_whenNoUsers() {
            // ARRANGE
            when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<UserDTO> result = userService.getAllUsers();
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle null values in update DTO gracefully")
        void shouldHandleNullValues_inUpdateDTO() {
            // ARRANGE
            UserDTO updateDTO = new UserDTO();
            updateDTO.setId(testEmployee.getId());
            updateDTO.setUsername(null);  // null username
            updateDTO.setEmail(null);     // null email
            updateDTO.setRole(testEmployee.getRole());

            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(userRepository.save(any(User.class)))
                .thenReturn(testEmployee);
            
            // ACT
            Optional<UserDTO> result = userService.updateUser(testEmployee.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            // Service should handle nulls gracefully - original values preserved
        }
    }
}
