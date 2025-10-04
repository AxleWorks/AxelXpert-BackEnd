package com.login.AxleXpert.auth;

import com.login.AxleXpert.checkstatus.CheckService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<CheckService.User, Long> {
    Optional<CheckService.User> findByUsername(String username);
}
