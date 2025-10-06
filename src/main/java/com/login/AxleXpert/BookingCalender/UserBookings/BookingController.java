package com.login.AxleXpert.BookingCalender.UserBookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getDummyBookings(@RequestParam(required = false) Integer count) {
        int min = 5;
        int max = 15;
        Random rnd = new Random();
        int size = (count != null) ? Math.max(min, Math.min(max, count)) : (rnd.nextInt(max - min + 1) + min);

        // prepare some sample services
        List<ServiceDto> services = List.of(
                new ServiceDto(1L, "Oil Change", new BigDecimal("29.99"), 30, "Basic oil change"),
                new ServiceDto(2L, "Tire Rotation", new BigDecimal("19.99"), 20, "Rotate tires"),
                new ServiceDto(3L, "Brake Inspection", new BigDecimal("49.99"), 45, "Inspect brakes")
        );

        // prepare some sample branches
        List<BranchDto> branches = List.of(
                new BranchDto(1L, "Central Branch", "123 Main St", "555-0100"),
                new BranchDto(2L, "North Branch", "456 North Ave", "555-0200"),
                new BranchDto(3L, "South Branch", "789 South Rd", "555-0300")
        );

        // sample users (customers and employees) - simplified to id and username only
        List<UserDto> customers = List.of(
                new UserDto(101L, "alice"),
                new UserDto(102L, "bob"),
                new UserDto(103L, "carol")
        );
        List<UserDto> employees = List.of(
                new UserDto(201L, "emp_john"),
                new UserDto(202L, "emp_jane")
        );

                List<BookingDto> bookings = new ArrayList<>();
        for (long i = 1; i <= size; i++) {
            ServiceDto svc = services.get(rnd.nextInt(services.size()));
            BranchDto br = branches.get(rnd.nextInt(branches.size()));
            UserDto cust = customers.get(rnd.nextInt(customers.size()));
            UserDto emp = employees.get(rnd.nextInt(employees.size()));

            LocalDateTime start = LocalDateTime.now().plusDays(rnd.nextInt(7)).plusHours(rnd.nextInt(8));
            LocalDateTime end = start.plusMinutes(svc.durationMinutes());

            BookingDto b = new BookingDto(
                    i,
                    cust.id(),
                    cust.username() + " Full",
                    "+1-555-100" + (int) (100 + i),
                    "Vehicle-" + (100 + i),
                    br.id(),
                    br.name(),
                    svc.id(),
                    svc.name(),
                    start,
                    end,
                    BookingStatus.PENDING,
                    emp.id(),
                    emp.username(),
                    svc.price(),
                    "Sample note " + i,
                    LocalDateTime.now().minusDays(rnd.nextInt(10)),
                    LocalDateTime.now()
            );
            bookings.add(b);
        }

        return ResponseEntity.ok(bookings);
    }

    // DTO records for demo response
        // simplified sample DTOs used for constructing flat BookingDto
        public static record UserDto(Long id, String username) {}

        public static record ServiceDto(Long id, String name, BigDecimal price, Integer durationMinutes, String description) {}

        public static record BranchDto(Long id, String name, String address, String phone) {}

        // flat booking DTO: no nested objects, only scalar fields (one-line values)
        public static record BookingDto(
                        Long id,
                        Long customerId,
                        String customerName,
                        String customerPhone,
                        String vehicle,
                        Long branchId,
                        String branchName,
                        Long serviceId,
                        String serviceName,
                        LocalDateTime startAt,
                        LocalDateTime endAt,
                        BookingStatus status,
                        Long assignedEmployeeId,
                        String assignedEmployeeName,
                        BigDecimal totalPrice,
                        String notes,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt
        ) {}
}
