package com.login.AxleXpert.notifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.notifications.entity.FirebaseToken;

@Repository
public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {
    Optional<FirebaseToken> findByToken(String token);
    
    List<FirebaseToken> findByUserId(Long userId);
    
    boolean existsByToken(String token);
    
    void deleteByToken(String token);
}
