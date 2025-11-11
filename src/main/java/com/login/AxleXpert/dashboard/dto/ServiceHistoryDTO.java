package com.login.AxleXpert.dashboard.dto;

import java.util.List;

public record ServiceHistoryDTO(
    List<ChartDataDTO> chartData,
    List<BreakdownItemDTO> breakdown
) {}