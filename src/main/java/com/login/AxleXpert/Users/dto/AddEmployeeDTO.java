package com.login.AxleXpert.Users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEmployeeDTO {
    private String email;
    private String role;
    private String branch;
}
