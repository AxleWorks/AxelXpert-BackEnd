package com.login.AxleXpert.bookings;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.bookings.dto.AssignEmployeeDTO;
import com.login.AxleXpert.bookings.dto.BookingDTO;
import com.login.AxleXpert.bookings.dto.RejectBookingDTO;
import com.login.AxleXpert.common.dto.ErrorResponse;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAll(@RequestParam(required = false) Integer count) {
        List<BookingDTO> dtos = bookingService.getAllBookings(count);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{bookingId}/assign")
    public ResponseEntity<?> assignEmployee(@PathVariable Long bookingId, @RequestBody AssignEmployeeDTO assignEmployeeDTO) {
        try {
            Optional<BookingDTO> result = bookingService.assignEmployee(bookingId, assignEmployeeDTO.employeeId());
            
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Booking not found with id: " + bookingId));
            }
            
            return ResponseEntity.ok(result.get());
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId, @RequestBody RejectBookingDTO rejectBookingDTO) {
        try {
            Optional<BookingDTO> result = bookingService.rejectBooking(bookingId, 
                    rejectBookingDTO.reason(), rejectBookingDTO.notes());
            
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Booking not found with id: " + bookingId));
            }
            
            return ResponseEntity.ok(result.get());
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}