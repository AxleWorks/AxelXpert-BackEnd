package com.login.AxleXpert.Users.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.dto.AddEmployeeDTO;
import com.login.AxleXpert.Users.dto.ChangePasswordDTO;
import com.login.AxleXpert.Users.dto.ProfileImageUpdateDTO;
import com.login.AxleXpert.Users.dto.UserDTO;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.EmailService;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final BookingRepository bookingRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      BranchRepository branchRepository,
                      BookingRepository bookingRepository,
                      TaskRepository taskRepository,
                      EmailService emailService,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.bookingRepository = bookingRepository;
        this.taskRepository = taskRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get the currently authenticated user from the security context
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    /**
     * Check if the current user is a manager
     */
    private boolean isCurrentUserManager() {
        return getCurrentUser()
            .map(user -> "manager".equalsIgnoreCase(user.getRole()))
            .orElse(false);
    }

    /**
     * Check if the current user is an admin
     */
    private boolean isCurrentUserAdmin() {
        return getCurrentUser()
            .map(user -> "admin".equalsIgnoreCase(user.getRole()))
            .orElse(false);
    }

    /**
     * Get the branch ID of the current user
     */
    private Optional<Long> getCurrentUserBranchId() {
        return getCurrentUser()
            .map(User::getBranch)
            .map(Branch::getId);
    }

    /**
     * Check if a user belongs to the same branch as the current user
     */
    private boolean isSameBranchAsCurrentUser(User user) {
        if (user == null || user.getBranch() == null) {
            return false;
        }
        return getCurrentUserBranchId()
            .map(currentBranchId -> currentBranchId.equals(user.getBranch().getId()))
            .orElse(false);
    }

    /**
     * Validate that the current user (if manager) can access the given user
     * - Admins can access all users
     * - Managers can access all customers (users) in any branch
     * - Managers can only access employees in their own branch
     */
    private boolean canAccessUser(User user) {
        if (isCurrentUserAdmin()) {
            return true; // Admins can access all users
        }
        if (isCurrentUserManager()) {
            // Managers can access all customers (users with role "user")
            if ("user".equalsIgnoreCase(user.getRole())) {
                return true;
            }
            // Managers can only access employees in their branch
            if ("employee".equalsIgnoreCase(user.getRole()) || "manager".equalsIgnoreCase(user.getRole())) {
                return isSameBranchAsCurrentUser(user);
            }
        }
        return true; // Other roles can access (will be controlled by endpoint security)
    }


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

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        // For "user" role (customers): both admin and manager can see all users in any branch
        if ("user".equalsIgnoreCase(role)) {
            return userRepository.findByRoleIgnoreCaseWithBranch(role).stream().map(this::toDto).toList();
        }
        
        // For "employee" role: managers can only see employees in their branch, admins can see all
        if ("employee".equalsIgnoreCase(role)) {
            if (isCurrentUserManager()) {
                return getCurrentUserBranchId()
                    .map(branchId -> userRepository.findByRoleIgnoreCaseAndBranch_IdWithBranch(role, branchId))
                    .orElse(List.of())
                    .stream()
                    .map(this::toDto)
                    .toList();
            }
        }
        
        // For other roles or if admin: return all users with the specified role
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
        Optional<User> userOpt = userRepository.findByIdWithBranch(id);
        
        // If current user is a manager, check if the requested user is in their branch
        if (isCurrentUserManager() && userOpt.isPresent()) {
            User user = userOpt.get();
            if (!canAccessUser(user)) {
                return Optional.empty(); // Manager cannot access users outside their branch
            }
        }
        
        return userOpt.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getEmployeesByBranch(Long branchId) {
        return userRepository.findByRoleIgnoreCaseAndBranch_IdWithBranch("employee", branchId).stream().map(this::toDto).toList();
    }

    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserDTO dto) {
        return userRepository.findById(id).map(user -> {
            // If current user is a manager, check if they can access this user
            if (isCurrentUserManager() && !canAccessUser(user)) {
                throw new SecurityException("You can only update users in your branch");
            }
            
            if (dto.getUsername() != null) user.setUsername(dto.getUsername());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getAddress() != null) user.setAddress(dto.getAddress());
            if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
            if (dto.getRole() != null) user.setRole(dto.getRole());
            if (dto.getIsActive() != null) user.setIs_Active(dto.getIsActive());
            if (dto.getIsBlocked() != null) user.setIs_Blocked(dto.getIsBlocked());

            if (dto.getBranchId() != null) {
                // If manager is trying to change branch, prevent it
                if (isCurrentUserManager() && !dto.getBranchId().equals(user.getBranch() != null ? user.getBranch().getId() : null)) {
                    throw new SecurityException("You cannot change the branch of a user");
                }
                branchRepository.findById(dto.getBranchId()).ifPresent(user::setBranch);
            }

            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    @Transactional
    public Optional<UserDTO> updateUsername(Long id, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Optional<User> existingUser = userRepository.findByUsername(newUsername.trim());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        return userRepository.findById(id).map(user -> {
            // If current user is a manager, check if they can access this user
            if (isCurrentUserManager() && !canAccessUser(user)) {
                throw new SecurityException("You can only update users in your branch");
            }
            
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
            // Use password encoder to verify current password
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return false;
            }
            if (dto.getNewPassword().length() < 6) {
                return false;
            }
            // Encode new password before saving
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
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
            // If current user is a manager, check if they can access this user
            if (isCurrentUserManager() && !canAccessUser(user)) {
                throw new SecurityException("You can only update users in your branch");
            }
            
            user.setProfileImageUrl(dto.getProfileImageUrl());
            user.setCloudinaryPublicId(dto.getCloudinaryPublicId());
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    @Transactional
    public Optional<UserDTO> deleteProfileImage(Long id) {
        return userRepository.findById(id).map(user -> {
            // If current user is a manager, check if they can access this user
            if (isCurrentUserManager() && !canAccessUser(user)) {
                throw new SecurityException("You can only update users in your branch");
            }
            
            user.setProfileImageUrl(null);
            user.setCloudinaryPublicId(null);
            User saved = userRepository.save(user);
            return toDto(saved);
        });
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public Optional<UserDTO> blockUser(Long id, boolean blocked) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // If current user is a manager, check if they can access this user
        if (isCurrentUserManager() && !canAccessUser(user)) {
            throw new SecurityException("You can only block/unblock users in your branch");
        }
        
        if (blocked) {
            boolean hasBookingsAsCustomer = bookingRepository.existsByCustomerId(id);
            boolean hasBookingsAsEmployee = bookingRepository.existsByAssignedEmployeeId(id);
            boolean hasTasks = taskRepository.existsByAssignedEmployeeId(id);
            
            if (hasBookingsAsCustomer || hasBookingsAsEmployee || hasTasks) {
                return Optional.empty();
            }
        }
        
        user.setIs_Blocked(blocked);
        User saved = userRepository.save(user);
        return Optional.of(toDto(saved));
    }

    @Transactional
    public boolean deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // If current user is a manager, check if they can access this user
        if (isCurrentUserManager() && !canAccessUser(user)) {
            throw new SecurityException("You can only delete users in your branch");
        }
        
        boolean hasBookingsAsCustomer = bookingRepository.existsByCustomerId(id);
        boolean hasBookingsAsEmployee = bookingRepository.existsByAssignedEmployeeId(id);
        boolean hasTasks = taskRepository.existsByAssignedEmployeeId(id);
        
        if (hasBookingsAsCustomer || hasBookingsAsEmployee || hasTasks) {
            return false;
        }
        
        userRepository.delete(user);
        return true;
    }

    @Transactional
    public UserDTO addEmployee(AddEmployeeDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        if (dto.getBranch() == null || dto.getBranch().trim().isEmpty()) {
            throw new IllegalArgumentException("Branch is required");
        }

        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Branch branch = branchRepository.findByName(dto.getBranch())
            .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + dto.getBranch()));

        // If current user is a manager, they can only add employees to their own branch
        if (isCurrentUserManager()) {
            Long currentUserBranchId = getCurrentUserBranchId()
                .orElseThrow(() -> new SecurityException("Manager must have a branch assigned"));
            
            if (!currentUserBranchId.equals(branch.getId())) {
                throw new SecurityException("You can only add employees to your own branch");
            }
        }

        String randomPassword = emailService.generateRandomPassword();

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setUsername(dto.getEmail());
        newUser.setRole(dto.getRole().toLowerCase());
        newUser.setBranch(branch);
        newUser.setToken(UUID.randomUUID().toString());
        // Encode the password before saving
        newUser.setPassword(passwordEncoder.encode(randomPassword));
        newUser.setIs_Active(true);
        newUser.setIs_Blocked(false);

        User saved = userRepository.save(newUser);
        
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
        }

        return toDto(saved);
    }
}
