package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);
    Optional<User> findByEmail(String email);

    // Convenience query methods used by UserController
    List<User> findByRoleIgnoreCase(String role);
    List<User> findByRoleIgnoreCaseAndBranch_Id(Long branchId);
}
