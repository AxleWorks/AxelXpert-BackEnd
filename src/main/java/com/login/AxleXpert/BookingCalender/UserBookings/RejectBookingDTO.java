package com.login.AxleXpert.BookingCalender.UserBookings;

/**
 * DTO for rejecting a booking
 */
public record RejectBookingDTO(
        String reason,
        String notes
) {}