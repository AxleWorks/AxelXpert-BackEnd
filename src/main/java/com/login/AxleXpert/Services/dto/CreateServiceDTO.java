package com.login.AxleXpert.Services.dto;

import java.math.BigDecimal;

public record CreateServiceDTO(
    String name,
    BigDecimal price,
    Integer durationMinutes,
    String description
) {}
