package com.login.AxleXpert.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO_Auth {
    private Long id;
    private String username;
    private String role;
    private String email;
    private Boolean isBlocked;
    private Boolean isActive;
//    UserDto + JWTToken
    private String JWTToken;

}
