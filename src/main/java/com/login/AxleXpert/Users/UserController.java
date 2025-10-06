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
    public ResponseEntity<List<User>> getEmployees() {
        List<User> employees = userRepository.findByRoleIgnoreCase("employee");
        return ResponseEntity.ok(employees);
    }

    // Get all users with role manager
    @GetMapping("/managers")
    public ResponseEntity<List<User>> getManagers() {
        List<User> managers = userRepository.findByRoleIgnoreCase("manager");
        return ResponseEntity.ok(managers);
    }

    // Get employees by branch id
    @GetMapping("/branch/{branchId}/employees")
    public ResponseEntity<List<User>> getEmployeesByBranch(@PathVariable Long branchId) {
        List<User> employees = userRepository.findByRoleIgnoreCaseAndBranch_Id(branchId);
        return ResponseEntity.ok(employees);
    }
}
