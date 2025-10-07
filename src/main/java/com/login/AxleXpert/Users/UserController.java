package com.login.AxleXpert.Users;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

}
