package com.login.AxleXpert.bookings.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Services.repository.ServiceRepository;
import com.login.AxleXpert.Tasks.service.TaskService;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.bookings.dto.BookingDTO;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.notifications.service.NotificationService;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final BranchRepository branchRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, TaskService taskService,
                          BranchRepository branchRepository, ServiceRepository serviceRepository, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.branchRepository = branchRepository;
        this.serviceRepository = serviceRepository;
        this.notificationService = notificationService;
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
        
        taskService.createTaskForBooking(bookingId, employeeId);
  
        notificationService.createAndSendNotification(employeeId, "New Task Assigned", "You have been assigned a new task!.", "EMPLOYEE");
        
        return Optional.of(toDto(savedBooking));
    }

    public Optional<BookingDTO> rejectBooking(Long bookingId, String reason, String notes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return Optional.empty();
        }

        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Cannot reject a booking that has been approved");
        }
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already rejected/cancelled");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        
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

    public boolean deleteBooking(Long bookingId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;
        bookingRepository.delete(bookingOpt.get());
        return true;
    }

    public BookingDTO createBooking(BookingDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Booking data is required");
        }

        if (dto.branchId() == null) {
            throw new IllegalArgumentException("branchId is required");
        }

        if (dto.serviceId() == null) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (dto.customerId() == null) {
            throw new IllegalArgumentException("customerId is required");
        }

        java.time.LocalDateTime startAt;
        if (dto.startAt() == null || dto.startAt().isBlank()) {
            startAt = java.time.LocalDateTime.now();
        } else {
            startAt = parseDateTime(dto.startAt());
        }

        if (bookingRepository.existsByBranch_IdAndStartAt(dto.branchId(), startAt)) {
            throw new IllegalStateException("Booking slot already taken for this branch and time");
        }

        var branchOpt = branchRepository.findById(dto.branchId());
        if (branchOpt.isEmpty()) {
            throw new IllegalArgumentException("Branch not found with id: " + dto.branchId());
        }

        var serviceOpt = serviceRepository.findById(dto.serviceId());
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found with id: " + dto.serviceId());
        }

        var customerOpt = userRepository.findById(dto.customerId());
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with id: " + dto.customerId());
        }

        Booking booking = new Booking();
        booking.setCustomer(customerOpt.get());
        booking.setCustomerName(dto.customerName() != null ? dto.customerName() : dto.customerId().toString());
        booking.setCustomerPhone(dto.customerPhone());
        booking.setVehicle(dto.vehicle());
        booking.setBranch(branchOpt.get());
        booking.setService(serviceOpt.get());
        booking.setStartAt(startAt);
        booking.setStatus(BookingStatus.PENDING);
        booking.setNotes(dto.notes());

        Booking saved = bookingRepository.save(booking);

        return toDto(saved);
    }

    private java.time.LocalDateTime parseDateTime(String input) {
        if (input == null) return null;
        String s = input.trim();
        try {
            java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(s);
            return odt.toLocalDateTime();
        } catch (java.time.format.DateTimeParseException ex) {
            try {
                java.time.Instant instant = java.time.Instant.parse(s);
                return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            } catch (java.time.DateTimeException ex2) {
                try {
                    return java.time.LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (java.time.format.DateTimeParseException ex3) {
                    throw new IllegalArgumentException("Unable to parse date/time: " + s);
                }
            }
        }
    }

    private BookingDTO toDto(Booking b) {
        Long customerId = b.getCustomer() != null ? b.getCustomer().getId() : null;
        Long branchId = b.getBranch() != null ? b.getBranch().getId() : null;
        Long serviceId = b.getService() != null ? b.getService().getId() : null;
        Long assignedEmployeeId = b.getAssignedEmployee() != null ? b.getAssignedEmployee().getId() : null;
        String branchName = b.getBranch() != null ? b.getBranch().getName() : null;
        String serviceName = b.getService() != null ? b.getService().getName() : null;
        String assignedEmployeeName = b.getAssignedEmployee() != null ? b.getAssignedEmployee().getUsername() : null;

        String startAtStr = b.getStartAt() != null ? b.getStartAt().toString() : null;
        String endAtStr = b.getEndAt() != null ? b.getEndAt().toString() : null;

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
                startAtStr,
                endAtStr,
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
