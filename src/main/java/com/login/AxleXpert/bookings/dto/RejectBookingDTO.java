package com.login.AxleXpert.bookings.dto;

/**
 * DTO for rejecting a booking
 */
public record RejectBookingDTO(
        String reason,
        String notes
) {}
