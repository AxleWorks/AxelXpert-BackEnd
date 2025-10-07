package com.login.AxleXpert.Users;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // Get all users with role employee
    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees() {
        List<User> employees = userRepository.findByRoleIgnoreCaseWithBranch("employee");
        List<UserDTO> dtos = employees.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    // Get all users with role manager
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers() {
        List<User> managers = userRepository.findByRoleIgnoreCaseWithBranch("manager");
        List<UserDTO> dtos = managers.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    // Get employees by branch id
    @GetMapping("/branch/{branchId}/employees")
    public ResponseEntity<List<UserDTO>> getEmployeesByBranch(@PathVariable Long branchId) {
        List<User> employees = userRepository.findByRoleIgnoreCaseAndBranch_IdWithBranch("employee", branchId);
        List<UserDTO> dtos = employees.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
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
            branchId,
            branchName,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
