package com.login.AxleXpert.Users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
}
