package com.login.AxleXpert.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);
    Optional<User> findByEmail(String email);

    // Convenience query methods used by UserController
    List<User> findByRoleIgnoreCase(String role);
    List<User> findByRoleIgnoreCaseAndBranch_Id(String role, Long branchId);

    // Fetch branch to avoid LazyInitializationException when converting to DTOs
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE LOWER(u.role) = LOWER(:role)")
    List<User> findByRoleIgnoreCaseWithBranch(@Param("role") String role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE LOWER(u.role) = LOWER(:role) AND b.id = :branchId")
    List<User> findByRoleIgnoreCaseAndBranch_IdWithBranch(@Param("role") String role, @Param("branchId") Long branchId);

    // Fetch a single user along with its branch to avoid LazyInitializationException
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE u.id = :id")
    Optional<User> findByIdWithBranch(@Param("id") Long id);

    // Fetch all active users (employees, managers, users) from a specific branch who have logged in
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE b.id = :branchId AND u.is_Active = true AND LOWER(u.role) IN ('employee', 'manager', 'user')")
    List<User> findActiveUsersByBranch(@Param("branchId") Long branchId);
}
