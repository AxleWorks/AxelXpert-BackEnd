package com.login.AxleXpert.Services.dto;

import java.math.BigDecimal;

import com.login.AxleXpert.Services.entity.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;

    public static ServiceDTO fromEntity(Service s) {
        if (s == null) return null;
        return new ServiceDTO(s.getId(), s.getName(), s.getPrice(), s.getDurationMinutes(), s.getDescription());
    }
}
