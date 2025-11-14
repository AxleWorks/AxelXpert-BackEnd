package com.login.AxleXpert.Users.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Tasks.entity.Task;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.Users.dto.AddEmployeeDTO;
import com.login.AxleXpert.Users.dto.ChangePasswordDTO;
import com.login.AxleXpert.Users.dto.ProfileImageUpdateDTO;
import com.login.AxleXpert.Users.dto.UserDTO;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.EmailService;
import com.login.AxleXpert.common.enums.TaskStatus;

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
                user.getUpdatedAt(),
                null // status not computed here
        );
    }

    private UserDTO toEmployeeDto(User user) {
        Long branchId = null;
        String branchName = null;
        if (user.getBranch() != null) {
            branchId = user.getBranch().getId();
            branchName = user.getBranch().getName();
        }
        // Check if employee has ongoing tasks
        long ongoingTasks = taskRepository.countByAssignedEmployeeIdAndStatusIn(
            user.getId(), 
            List.of(TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS, TaskStatus.ON_HOLD)
        );
        String status = ongoingTasks > 0 ? "busy" : "available";
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
                user.getUpdatedAt(),
                status
        );
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCaseWithBranch(role).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getEmployees() {
        return userRepository.findByRoleIgnoreCaseWithBranch("employee").stream().map(this::toEmployeeDto).toList();
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
            if (dto.getUsername() != null) user.setUsername(dto.getUsername());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getAddress() != null) user.setAddress(dto.getAddress());
            if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
            if (dto.getRole() != null) user.setRole(dto.getRole());
            if (dto.getIsActive() != null) user.setIs_Active(dto.getIsActive());
            if (dto.getIsBlocked() != null) user.setIs_Blocked(dto.getIsBlocked());

            if (dto.getBranchId() != null) {
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
            if (!Objects.equals(user.getPassword(), dto.getCurrentPassword())) {
                return false;
            }
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

    @Transactional
    public Optional<UserDTO> blockUser(Long id, boolean blocked) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
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
        
        // Check for ongoing tasks (not completed) for the user's bookings
        long ongoingAsCustomer = taskRepository.countByCustomerIdAndStatusIn(id, List.of(TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS, TaskStatus.ON_HOLD));
        long ongoingAsEmployee = taskRepository.countByAssignedEmployeeIdAndStatusIn(id, List.of(TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS, TaskStatus.ON_HOLD));
        
        if (ongoingAsCustomer + ongoingAsEmployee > 0) {
            return false;
        }
        
        // Delete all tasks where user is assigned employee or customer of the booking
        Set<Task> tasksToDelete = new LinkedHashSet<>();
        tasksToDelete.addAll(taskRepository.findByAssignedEmployeeIdWithBooking(id));
        tasksToDelete.addAll(taskRepository.findByCustomerId(id));
        for (Task task : tasksToDelete) {
            taskRepository.delete(task);
        }
        
        // Delete all bookings where user is customer
        List<Booking> bookingsToDelete = bookingRepository.findByCustomerId(id);
        for (Booking booking : bookingsToDelete) {
            bookingRepository.delete(booking);
        }
        
        // Finally, delete the user
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

        String randomPassword = emailService.generateRandomPassword();

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setUsername(dto.getEmail());
        newUser.setRole(dto.getRole().toUpperCase());
        newUser.setBranch(branch);
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setPassword(randomPassword);
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
