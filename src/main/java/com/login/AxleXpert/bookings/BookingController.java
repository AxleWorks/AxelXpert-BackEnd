package com.login.AxleXpert.bookings;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.login.AxleXpert.Services.ServiceRepository;
import com.login.AxleXpert.Users.UserRepository;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public BookingController(BookingService bookingService, UserRepository userRepository, ServiceRepository serviceRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
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

    @PostMapping("")
    public ResponseEntity<?> createBooking(@RequestBody com.login.AxleXpert.bookings.dto.CreateBookingRequest req) {
        try {
            // Map CreateBookingRequest (frontend shape) to internal BookingDTO
            Long branchId = req.branch();

            // Resolve customer: may be an ID or a username string
            Long customerId = null;
            String customerRaw = req.customer();
            if (customerRaw != null) {
                String cr = customerRaw.trim();
                if (cr.matches("\\d+")) {
                    customerId = Long.parseLong(cr);
                } else {
                    var userOpt = userRepository.findByUsername(cr);
                    if (userOpt.isPresent()) {
                        customerId = userOpt.get().getId();
                    } else {
                        throw new IllegalArgumentException("Customer not found with identifier: " + cr);
                    }
                }
            }

            // Resolve service: may be an ID or a name string
            Long serviceId = null;
            String serviceRaw = req.service();
            if (serviceRaw != null) {
                String sr = serviceRaw.trim();
                if (sr.matches("\\d+")) {
                    serviceId = Long.parseLong(sr);
                } else {
                    var svcOpt = serviceRepository.findByNameIgnoreCase(sr);
                    if (svcOpt.isPresent()) {
                        serviceId = svcOpt.get().getId();
                    } else {
                        throw new IllegalArgumentException("Service not found with name: " + sr);
                    }
                }
            }

            // Build startAt from provided date/time fields when possible.
            String startAtStr = null;
            if (req.date() != null && !req.date().isBlank()) {
                String date = req.date().trim();
                if (date.contains("T")) {
                    // full timestamp provided
                    startAtStr = date;
                } else {
                    // date only (e.g. 2025-10-27) -> combine with time if present
                    if (req.time() != null && !req.time().isBlank()) {
                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(date);
                            java.time.format.DateTimeFormatter tf = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
                            java.time.LocalTime t = java.time.LocalTime.parse(req.time().trim(), tf);
                            startAtStr = java.time.LocalDateTime.of(d, t).toString();
                        } catch (Exception ex) {
                            // fallback: keep date string
                            startAtStr = date;
                        }
                    } else {
                        startAtStr = date;
                    }
                }
            }

            com.login.AxleXpert.bookings.dto.BookingDTO bookingDTO = new com.login.AxleXpert.bookings.dto.BookingDTO(
                    null,
                    customerId,
                    req.customerName(),
                    req.customerPhone(),
                    req.vehicle(),
                    branchId,
                    null,
                    serviceId,
                    null,
                    startAtStr,
                    null,
                    req.status() != null ? com.login.AxleXpert.common.enums.BookingStatus.fromString(req.status()) : null,
                    null,
                    null,
                    req.totalPrice(),
                    req.notes(),
                    null,
                    null
            );

            BookingDTO created = bookingService.createBooking(bookingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Validation error creating booking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create booking: " + e.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@org.springframework.web.bind.annotation.PathVariable Long id) {
        try {
            boolean deleted = bookingService.deleteBooking(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Booking not found with id: " + id));
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete booking: " + e.getMessage()));
        }
    }
}