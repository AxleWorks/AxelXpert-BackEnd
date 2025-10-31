<<<<<<<< HEAD:src/main/java/com/login/AxleXpert/auth/DTO/PasswordResetRequestDTO.java

package com.login.AxleXpert.auth.dto;
========
package com.login.AxleXpert.auth;
>>>>>>>> parent of 11a35c4 (Merge branch 'main' of https://github.com/AxleWorks/AxelXpert-BackEnd):src/main/java/com/login/AxleXpert/auth/SignupDTO.java

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {
    
    private String email;
}