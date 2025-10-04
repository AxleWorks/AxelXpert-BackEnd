package com.login.AxleXpert.Users;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // numeric id (kept for compatibility with the mock)

    @Column(unique = true, nullable = false)
    private String username;

    // Store the hashed password here. Keep the column name 'password' to avoid breaking existing code.
    @Column(nullable = false)
    private String password;

    // Role: "user", "employee", "manager"
    @Column(nullable = false)
    private String role;

    @Column(unique = true)
    private String email;

    private Boolean is_Blocked = false;

    // Availability active (true = available)
    @Column(nullable = false)
    private Boolean is_Active = false;

    // 32 character token (e.g. API or session token). Keep length 64 to be safe for future encodings.
    @Column(length = 64, unique = true)
    private String token;

    private String address;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
