package com.login.AxleXpert.Users.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String role;
    private String email;
    private Boolean isBlocked;
    private Boolean isActive;
    private String address;
    private String phoneNumber;
    private String profileImageUrl;
    private String cloudinaryPublicId;
    private Long branchId;
    private String branchName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
