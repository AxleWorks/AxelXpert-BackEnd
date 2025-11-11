package com.login.AxleXpert.dashboard.dto;

public record RecentBookingDTO(
    Long id,
    String customer,
    String service,
    String branch,
    String status,
    String date,
    String amount
) {}