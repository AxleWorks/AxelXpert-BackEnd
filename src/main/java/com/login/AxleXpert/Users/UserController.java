package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Add new employee
    @PostMapping("/add-employee")
    public ResponseEntity<?> addEmployee(@RequestBody AddEmployeeDTO dto) {
        if (dto == null || dto.getEmail() == null || dto.getRole() == null || dto.getBranch() == null) {
            return ResponseEntity.badRequest().body("Email, role, and branch are required");
        }

        try {
            UserDTO created = userService.addEmployee(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Check if it's an email failure
            if (e.getMessage() != null && e.getMessage().contains("Failed to send welcome email")) {
                return ResponseEntity.status(500).body("User created but email failed: " + e.getMessage());
            }
            return ResponseEntity.internalServerError().body("Failed to create employee: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to create employee: " + e.getMessage());
        }
    }

    // Get all users with role employee
    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees() {
        return ResponseEntity.ok(userService.getEmployees());
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    // Get all users with a role manager
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    // Get all users with role manager
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers() {
        return ResponseEntity.ok(userService.getManagers());
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update user by id
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
    return userService.updateUser(id, userDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update username - allows employees to change their username after login
    @PutMapping("/{id}/username")
    public ResponseEntity<?> updateUsername(@PathVariable Long id, @RequestBody UpdateUsernameDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        try {
            return userService.updateUsername(id, dto.getUsername())
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Change password for a user: requires current password and new password
    @PutMapping("/{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO dto) {
        if (dto == null || dto.getCurrentPassword() == null || dto.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("currentPassword and newPassword are required");
        }

        // Attempt change, service returns empty when user not found or validation fails
        boolean ok = userService.changePassword(id, dto);
        if (ok) return ResponseEntity.ok("Password changed");
        return ResponseEntity.badRequest().body("Invalid current password, user not found, or new password too weak");
    }

    // Get employees by branch id
    @GetMapping("/branch/{branchId}/employees")
    public ResponseEntity<List<UserDTO>> getEmployeesByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(userService.getEmployeesByBranch(branchId));
    }

    // Update profile image
    @PutMapping("/{id}/profile-image")
    public ResponseEntity<UserDTO> updateProfileImage(@PathVariable Long id, @RequestBody ProfileImageUpdateDTO dto) {
        return userService.updateProfileImage(id, dto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // Delete profile image
    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<UserDTO> deleteProfileImage(@PathVariable Long id) {
        return userService.deleteProfileImage(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Block or unblock user by id
    @PutMapping("/{id}/block")
    public ResponseEntity<?> updateBlockStatus(@PathVariable Long id, @RequestBody BlockStatusDTO dto) {
        if (dto == null || dto.getBlocked() == null) {
            return ResponseEntity.badRequest().body("'blocked' field is required (true or false)");
        }

        try {
            Optional<UserDTO> result = userService.blockUser(id, dto.getBlocked());
            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            } else {
                return ResponseEntity.status(409)
                    .body("Cannot block user: User has active bookings or tasks");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(409)
                    .body("Cannot delete user: User has active bookings or tasks");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
