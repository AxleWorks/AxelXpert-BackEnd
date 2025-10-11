package com.login.AxleXpert.bookings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Tasks.service.TaskService;
import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;
import com.login.AxleXpert.bookings.dto.BookingDTO;
import com.login.AxleXpert.common.enums.BookingStatus;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, TaskService taskService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings(Integer count) {
        List<BookingDTO> dtos = getAllBookings();
        
        if (count != null && count > 0) {
            int n = Math.max(0, Math.min(dtos.size(), count));
            return dtos.subList(0, n);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public Optional<BookingDTO> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toDto);
    }

    public Optional<BookingDTO> assignEmployee(Long bookingId, Long employeeId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return Optional.empty();
        }

        Booking booking = bookingOpt.get();
        
        // Check if booking can be approved
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot approve a booking that has been rejected/cancelled");
        }
        
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Booking is already approved");
        }

        Optional<User> employeeOpt = userRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with id: " + employeeId);
        }

        User employee = employeeOpt.get();
        if (!"EMPLOYEE".equalsIgnoreCase(employee.getRole())) {
            throw new IllegalArgumentException("User with id " + employeeId + " is not an employee");
        }

        booking.setAssignedEmployee(employee);
        booking.setStatus(BookingStatus.APPROVED);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Create a task for the assigned employee
        taskService.createTaskForBooking(bookingId, employeeId);
        
        return Optional.of(toDto(savedBooking));
    }

    public Optional<BookingDTO> rejectBooking(Long bookingId, String reason, String notes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return Optional.empty();
        }

        Booking booking = bookingOpt.get();
        
        // Check if booking can be rejected
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Cannot reject a booking that has been approved");
        }
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already rejected/cancelled");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        
        // Update notes with rejection reason
        String existingNotes = booking.getNotes() != null ? booking.getNotes() : "";
        StringBuilder updatedNotes = new StringBuilder(existingNotes);
        
        if (reason != null && !reason.trim().isEmpty()) {
            if (!existingNotes.isEmpty()) {
                updatedNotes.append("\n");
            }
            updatedNotes.append("Rejection reason: ").append(reason);
        }
        
        if (notes != null && !notes.trim().isEmpty()) {
            if (updatedNotes.length() > 0) {
                updatedNotes.append("\n");
            }
            updatedNotes.append("Additional notes: ").append(notes);
        }
        
        booking.setNotes(updatedNotes.toString());
        
        Booking savedBooking = bookingRepository.save(booking);
        return Optional.of(toDto(savedBooking));
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