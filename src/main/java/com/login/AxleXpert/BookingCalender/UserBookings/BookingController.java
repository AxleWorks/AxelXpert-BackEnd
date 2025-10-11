package com.login.AxleXpert.BookingCalender.UserBookings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAll(@RequestParam(required = false) Integer count) {
        List<BookingDTO> dtos = bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (count != null && count > 0) {
            int n = Math.max(0, Math.min(dtos.size(), count));
            return ResponseEntity.ok(dtos.subList(0, n));
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{bookingId}/assign")
    public ResponseEntity<?> assignEmployee(@PathVariable Long bookingId, @RequestBody AssignEmployeeDTO assignEmployeeDTO) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Booking not found with id: " + bookingId));
        }

        Optional<User> employeeOpt = userRepository.findById(assignEmployeeDTO.employeeId());
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Employee not found with id: " + assignEmployeeDTO.employeeId()));
        }

        User employee = employeeOpt.get();
        if (!"EMPLOYEE".equalsIgnoreCase(employee.getRole())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("User with id " + assignEmployeeDTO.employeeId() + " is not an employee"));
        }

        Booking booking = bookingOpt.get();
        booking.setAssignedEmployee(employee);
        booking.setStatus(BookingStatus.APPROVED);
        
        Booking savedBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(toDto(savedBooking));
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId, @RequestBody RejectBookingDTO rejectBookingDTO) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Booking not found with id: " + bookingId));
        }

        Booking booking = bookingOpt.get();
        booking.setStatus(BookingStatus.CANCELLED);
        
        // Update notes with rejection reason if provided
        String existingNotes = booking.getNotes() != null ? booking.getNotes() : "";
        StringBuilder updatedNotes = new StringBuilder(existingNotes);
        
        if (rejectBookingDTO.reason() != null && !rejectBookingDTO.reason().trim().isEmpty()) {
            if (!existingNotes.isEmpty()) {
                updatedNotes.append("\n");
            }
            updatedNotes.append("Rejection reason: ").append(rejectBookingDTO.reason());
        }
        
        if (rejectBookingDTO.notes() != null && !rejectBookingDTO.notes().trim().isEmpty()) {
            if (updatedNotes.length() > 0) {
                updatedNotes.append("\n");
            }
            updatedNotes.append("Additional notes: ").append(rejectBookingDTO.notes());
        }
        
        booking.setNotes(updatedNotes.toString());
        
        Booking savedBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(toDto(savedBooking));
    }

    private BookingDTO toDto(Booking b) {
        Long customerId = b.getCustomer() != null ? b.getCustomer().getId() : null;
        Long branchId = b.getBranch() != null ? b.getBranch().getId() : null;
        Long serviceId = b.getService() != null ? b.getService().getId() : null;
        Long assignedEmployeeId = b.getAssignedEmployee() != null ? b.getAssignedEmployee().getId() : null;
        String branchName = b.getBranch() != null ? b.getBranch().getName() : null;
        String serviceName = b.getService() != null ? b.getService().getName() : null;
        String assignedEmployeeName = b.getAssignedEmployee() != null ? b.getAssignedEmployee().getUsername() : null;

        return new BookingDTO(
                b.getId(),
                customerId,
                b.getCustomerName(),
                b.getCustomerPhone(),
                b.getVehicle(),
                branchId,
                branchName,
                serviceId,
                serviceName,
                b.getStartAt(),
                b.getEndAt(),
                b.getStatus(),
                assignedEmployeeId,
                assignedEmployeeName,
                b.getTotalPrice(),
                b.getNotes(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
