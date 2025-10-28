package com.login.AxleXpert.bookings.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * DTO representing the create booking payload sent by the frontend.
 * Accepts field names like `branch`, `customer`, `service`, `date`, `time`, etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateBookingRequest(
        Long branch,
        String customer,
        String service,
        String date,
        String time,
        String vehicle,
        String status,
        String notes,
        String customerName,
        String customerPhone,
        BigDecimal totalPrice
) {}
