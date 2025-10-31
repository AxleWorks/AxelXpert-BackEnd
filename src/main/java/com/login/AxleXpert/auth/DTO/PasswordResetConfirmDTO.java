package com.login.AxleXpert.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirmDTO {
    
    private String token;
    private String newPassword;
}