package com.login.AxleXpert.notifications.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationReadStatusDTO {
    @NotNull(message = "isRead field is required")
    private Boolean isRead;
}
