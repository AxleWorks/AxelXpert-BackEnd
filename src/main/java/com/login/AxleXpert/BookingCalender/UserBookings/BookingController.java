package com.login.AxleXpert.BookingCalender.UserBookings;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
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
