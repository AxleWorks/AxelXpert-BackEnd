package com.login.AxleXpert.Users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUpdateDTO {
    private String profileImageUrl;
    private String cloudinaryPublicId;
}
