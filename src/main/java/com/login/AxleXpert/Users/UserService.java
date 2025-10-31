package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Branches.Branch;
import com.login.AxleXpert.Branches.BranchRepository;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.bookings.BookingRepository;
import com.login.AxleXpert.common.EmailService;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final BookingRepository bookingRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, 
                      BranchRepository branchRepository,
                      BookingRepository bookingRepository,
                      TaskRepository taskRepository,
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.bookingRepository = bookingRepository;
        this.taskRepository = taskRepository;
        this.emailService = emailService;
    }
    // Convert entity -> DTO
    private UserDTO toDto(User user) {
        Long branchId = null;
        String branchName = null;
        if (user.getBranch() != null) {
            branchId = user.getBranch().getId();
            branchName = user.getBranch().getName();
        }
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getIs_Blocked(),
                user.getIs_Active(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getCloudinaryPublicId(),
                branchId,
                branchName,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Fetch users by role and map to DTOs
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCaseWithBranch(role).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getEmployees() {
        return getUsersByRole("employee");
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsers() {
        return getUsersByRole("user");
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getManagers() {
        return getUsersByRole("manager");
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findByIdWithBranch(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getEmployeesByBranch(Long branchId) {
        return userRepository.findByRoleIgnoreCaseAndBranch_IdWithBranch("employee", branchId).stream().map(this::toDto).toList();
    }

    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserDTO dto) {
        return userRepository.findById(id).map(user -> {
            // Only update fields that are present in DTO (basic overwrite behavior)
            if (dto.getUsername() != null) user.setUsername(dto.getUsername());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getAddress() != null) user.setAddress(dto.getAddress());
            if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
            if (dto.getRole() != null) user.setRole(dto.getRole());
            if (dto.getIsActive() != null) user.setIs_Active(dto.getIsActive());
            if (dto.getIsBlocked() != null) user.setIs_Blocked(dto.getIsBlocked());

            // Update branch association if provided
            if (dto.getBranchId() != null) {
                branchRepository.findById(dto.getBranchId()).ifPresent(user::setBranch);
            }

            // Save and return DTO
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    @Transactional
    public Optional<UserDTO> updateUsername(Long id, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // Check if username already exists (excluding current user)
        Optional<User> existingUser = userRepository.findByUsername(newUsername.trim());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        return userRepository.findById(id).map(user -> {
            user.setUsername(newUsername.trim());
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    @Transactional
    public boolean changePassword(Long id, ChangePasswordDTO dto) {
        if (dto == null || dto.getCurrentPassword() == null || dto.getNewPassword() == null) {
            return false;
        }
        return userRepository.findById(id).map(user -> {
            // Validate current password (plain-text compare as repository stores raw password)
            if (!Objects.equals(user.getPassword(), dto.getCurrentPassword())) {
                // indicate validation failure
                return false;
            }
            // Basic validation for new password length
            if (dto.getNewPassword().length() < 6) {
                return false;
            }
            user.setPassword(dto.getNewPassword());
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Transactional
    public Optional<UserDTO> updateProfileImage(Long id, ProfileImageUpdateDTO dto) {
        if (dto == null || dto.getProfileImageUrl() == null || dto.getCloudinaryPublicId() == null) {
            return Optional.empty();
        }
        
        return userRepository.findById(id).map(user -> {
            user.setProfileImageUrl(dto.getProfileImageUrl());
            user.setCloudinaryPublicId(dto.getCloudinaryPublicId());
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    @Transactional
    public Optional<UserDTO> deleteProfileImage(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setProfileImageUrl(null);
            user.setCloudinaryPublicId(null);
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    //Block or unblock a user by ID.
    @Transactional
    public Optional<UserDTO> blockUser(Long id, boolean blocked) {
        // Check if user exists
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // If trying to block (not unblock), check for dependencies
        if (blocked) {
            // Check if user has any bookings (as customer or assigned employee)
            boolean hasBookingsAsCustomer = bookingRepository.existsByCustomerId(id);
            boolean hasBookingsAsEmployee = bookingRepository.existsByAssignedEmployeeId(id);
            
            // Check if user has any assigned tasks
            boolean hasTasks = taskRepository.existsByAssignedEmployeeId(id);
            
            // User cannot be blocked if they have bookings or tasks
            if (hasBookingsAsCustomer || hasBookingsAsEmployee || hasTasks) {
                return Optional.empty();
            }
        }
        
        // Safe to update block status
        user.setIs_Blocked(blocked);
        User saved = userRepository.save(user);
        return Optional.of(toDto(saved));
    }

    //Delete a user by ID.
    @Transactional
    public boolean deleteUser(Long id) {
        // Check if user exists
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if user has any bookings (as customer or assigned employee)
        boolean hasBookingsAsCustomer = bookingRepository.existsByCustomerId(id);
        boolean hasBookingsAsEmployee = bookingRepository.existsByAssignedEmployeeId(id);
        
        // Check if user has any assigned tasks
        boolean hasTasks = taskRepository.existsByAssignedEmployeeId(id);
        
        // User cannot be deleted if they have bookings or tasks
        if (hasBookingsAsCustomer || hasBookingsAsEmployee || hasTasks) {
            return false;
        }
        
        // Safe to delete
        userRepository.delete(user);
        return true;
    }

    // Add a new employee
    @Transactional
    public UserDTO addEmployee(AddEmployeeDTO dto) {
        // Validate input
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        if (dto.getBranch() == null || dto.getBranch().trim().isEmpty()) {
            throw new IllegalArgumentException("Branch is required");
        }

        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Find branch by name
        Branch branch = branchRepository.findByName(dto.getBranch())
            .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + dto.getBranch()));

        // Generate random 6-character password
        String randomPassword = emailService.generateRandomPassword();

        // Create new user
        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setUsername(dto.getEmail()); // Use email as username
        newUser.setRole(dto.getRole().toUpperCase());
        newUser.setBranch(branch);
        
        // Generate a random token for activation
        newUser.setToken(UUID.randomUUID().toString());
        
        // Set the generated random password
        newUser.setPassword(randomPassword);
        
        // Set initial states
        newUser.setIs_Active(true); // User can login immediately
        newUser.setIs_Blocked(false);

        // Save user to database
        User saved = userRepository.save(newUser);
        
        // Send welcome email with credentials
        try {
            log.info("Sending welcome email to new employee: {}", saved.getEmail());
            emailService.sendWelcomeEmail(
                saved.getEmail(),
                randomPassword,
                saved.getRole(),
                branch.getName()
            );
            log.info("Welcome email sent successfully to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", saved.getEmail(), e.getMessage(), e);
            // If email fails, still return the created user but log the error
        }

        return toDto(saved);
    }
}
