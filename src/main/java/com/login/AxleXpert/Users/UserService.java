package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Branches.BranchRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public UserService(UserRepository userRepository, BranchRepository branchRepository) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
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
}
