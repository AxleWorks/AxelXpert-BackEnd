package com.login.AxleXpert.Users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.login.AxleXpert.Users.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);
    Optional<User> findByEmail(String email);

    List<User> findByRoleIgnoreCase(String role);
    List<User> findByRoleIgnoreCaseAndBranch_Id(String role, Long branchId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE LOWER(u.role) = LOWER(:role)")
    List<User> findByRoleIgnoreCaseWithBranch(@Param("role") String role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE LOWER(u.role) = LOWER(:role) AND b.id = :branchId")
    List<User> findByRoleIgnoreCaseAndBranch_IdWithBranch(@Param("role") String role, @Param("branchId") Long branchId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch b WHERE u.id = :id")
    Optional<User> findByIdWithBranch(@Param("id") Long id);
}
