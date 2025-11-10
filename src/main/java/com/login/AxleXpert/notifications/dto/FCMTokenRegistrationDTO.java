package com.login.AxleXpert.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenRegistrationDTO {
    @NotBlank(message = "Firebase token is required")
    private String token;
}
