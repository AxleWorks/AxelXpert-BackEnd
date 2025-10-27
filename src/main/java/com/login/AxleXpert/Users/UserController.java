package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    // Get all users with role employee
    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees() {
        return ResponseEntity.ok(userService.getEmployees());
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

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
