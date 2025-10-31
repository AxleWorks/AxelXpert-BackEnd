package com.login.AxleXpert.Services.dto;

import java.math.BigDecimal;

public record UpdateServiceDTO(
    String name,
    BigDecimal price,
    Integer durationMinutes,
    String description
) {}
