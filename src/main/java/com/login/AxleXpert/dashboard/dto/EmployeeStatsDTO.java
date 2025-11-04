package com.login.AxleXpert.dashboard.dto;

public record EmployeeStatsDTO(
    StatsItemDTO vehicles,
    StatsItemDTO services,
    StatsItemDTO workHours,
    StatsItemDTO completionRate,
    StatsItemDTO upcomingTasks
) {}