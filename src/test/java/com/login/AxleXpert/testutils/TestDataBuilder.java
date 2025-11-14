package com.login.AxleXpert.testutils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Services.entity.Service;
import com.login.AxleXpert.Tasks.entity.Task;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Vehicals.entity.Vehicle;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.common.enums.BookingStatus;
import com.login.AxleXpert.common.enums.TaskStatus;

/**
 * Test Data Builder - Factory class for creating test entities
 * 
 * LEARNING: Builder Pattern
 * - Provides a clean way to create complex objects
 * - Uses method chaining for readability
 * - Provides default values, but allows customization
 * 
 * Example usage:
 *   User user = TestDataBuilder.createUser().withEmail("custom@test.com").build();
 */
public class TestDataBuilder {

    // ===================================================================
    // USER BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic User entity with default customer role
     * 
     * LEARNING: Why separate methods for each entity type?
     * - Each entity has different required fields
     * - Makes it clear what kind of object you're creating
     * - Easier to maintain when entity structure changes
     */
    public static User createUser() {
        User user = new User();
        user.setId(TestConstants.TEST_USER_ID);
        user.setUsername(TestConstants.TEST_USERNAME);
        user.setEmail(TestConstants.TEST_EMAIL);
        user.setPassword(TestConstants.TEST_ENCRYPTED_PASSWORD);
        user.setRole(TestConstants.ROLE_CUSTOMER);
        user.setPhoneNumber(TestConstants.TEST_PHONE);
        user.setAddress(TestConstants.TEST_ADDRESS);
        user.setIs_Blocked(false);
        user.setIs_Active(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setVehicles(new ArrayList<>());
        return user;
    }
    
    /**
     * Creates a User with specific ID
     * Useful when you need multiple users with different IDs
     */
    public static User createUser(Long id) {
        User user = createUser();
        user.setId(id);
        user.setUsername("user" + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }
    
    /**
     * Creates a User with specific ID and role
     * LEARNING: Method overloading allows different combinations
     */
    public static User createUser(Long id, String role) {
        User user = createUser(id);
        user.setRole(role);
        return user;
    }
    
    /**
     * Creates a customer user (most common test case)
     */
    public static User createCustomer() {
        return createUser(1L, TestConstants.ROLE_CUSTOMER);
    }
    
    /**
     * Creates an employee user
     */
    public static User createEmployee() {
        User employee = createUser(2L, TestConstants.ROLE_EMPLOYEE);
        employee.setUsername("employee");
        employee.setEmail("employee@example.com");
        return employee;
    }
    
    /**
     * Creates a manager user
     */
    public static User createManager() {
        User manager = createUser(3L, TestConstants.ROLE_MANAGER);
        manager.setUsername("manager");
        manager.setEmail("manager@example.com");
        return manager;
    }
    
    /**
     * Creates a blocked user (for testing blocked user scenarios)
     */
    public static User createBlockedUser() {
        User user = createUser();
        user.setIs_Blocked(true);
        return user;
    }
    
    /**
     * Creates an inactive user (for testing activation scenarios)
     */
    public static User createInactiveUser() {
        User user = createUser();
        user.setIs_Active(false);
        user.setToken(TestConstants.TEST_ACTIVATION_TOKEN);
        return user;
    }

    // ===================================================================
    // SERVICE BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic Service entity
     * 
     * LEARNING: Services represent the types of automotive work
     * Examples: Oil Change, Brake Service, Tire Rotation
     */
    public static Service createService() {
        Service service = new Service();
        service.setId(TestConstants.TEST_SERVICE_ID);
        service.setName(TestConstants.TEST_SERVICE_NAME);
        service.setPrice(new BigDecimal(TestConstants.TEST_SERVICE_PRICE));
        service.setDurationMinutes(TestConstants.TEST_SERVICE_DURATION);
        service.setDescription("Standard oil change service");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        service.setBookings(new ArrayList<>());
        service.setServiceSubTasks(new ArrayList<>());
        return service;
    }
    
    /**
     * Creates a Service with specific ID
     */
    public static Service createService(Long id) {
        Service service = createService();
        service.setId(id);
        service.setName("Service " + id);
        return service;
    }
    
    /**
     * Creates a Service with specific ID, name, and price
     */
    public static Service createService(Long id, String name, BigDecimal price) {
        Service service = createService(id);
        service.setName(name);
        service.setPrice(price);
        return service;
    }

    // ===================================================================
    // BRANCH BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic Branch entity
     * 
     * LEARNING: Branches represent physical service locations
     * Each branch can have its own manager, bookings, and employees
     */
    public static Branch createBranch() {
        Branch branch = new Branch();
        branch.setId(TestConstants.TEST_BRANCH_ID);
        branch.setName(TestConstants.TEST_BRANCH_NAME);
        branch.setAddress(TestConstants.TEST_BRANCH_ADDRESS);
        branch.setPhone(TestConstants.TEST_BRANCH_PHONE);
        branch.setEmail(TestConstants.TEST_BRANCH_EMAIL);
        branch.setOpenHours("08:00");
        branch.setCloseHours("18:00");
        branch.setCreatedAt(LocalDateTime.now());
        branch.setUpdatedAt(LocalDateTime.now());
        branch.setBookings(new ArrayList<>());
        return branch;
    }
    
    /**
     * Creates a Branch with specific ID
     */
    public static Branch createBranch(Long id) {
        Branch branch = createBranch();
        branch.setId(id);
        branch.setName("Branch " + id);
        return branch;
    }
    
    /**
     * Creates a Branch with a manager assigned
     */
    public static Branch createBranchWithManager(User manager) {
        Branch branch = createBranch();
        branch.setManager(manager);
        return branch;
    }

    // ===================================================================
    // BOOKING BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic Booking entity
     * 
     * LEARNING: Bookings are appointments made by customers
     * They link together: Customer, Service, Branch, and optionally an Employee
     */
    public static Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(TestConstants.TEST_BOOKING_ID);
        booking.setCustomer(createCustomer());
        booking.setCustomerName(TestConstants.TEST_CUSTOMER_NAME);
        booking.setCustomerPhone(TestConstants.TEST_PHONE);
        booking.setVehicle(TestConstants.TEST_VEHICLE);
        booking.setService(createService());
        booking.setBranch(createBranch());
        booking.setStartAt(LocalDateTime.now().plusDays(1)); // Tomorrow
        booking.setEndAt(LocalDateTime.now().plusDays(1).plusMinutes(30)); // 30 min duration
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(new BigDecimal(TestConstants.TEST_SERVICE_PRICE));
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return booking;
    }
    
    /**
     * Creates a Booking with specific status
     * LEARNING: Testing different booking statuses is critical
     */
    public static Booking createBooking(BookingStatus status) {
        Booking booking = createBooking();
        booking.setStatus(status);
        return booking;
    }
    
    /**
     * Creates a complete Booking with all relationships
     */
    public static Booking createBooking(User customer, Service service, Branch branch) {
        Booking booking = createBooking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBranch(branch);
        return booking;
    }
    
    /**
     * Creates an approved booking with assigned employee
     */
    public static Booking createApprovedBooking() {
        Booking booking = createBooking(BookingStatus.APPROVED);
        booking.setAssignedEmployee(createEmployee());
        return booking;
    }

    // ===================================================================
    // TASK BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic Task entity
     * 
     * LEARNING: Tasks are work items assigned to employees
     * They're created from bookings and track the actual service work
     */
    public static Task createTask() {
        Task task = new Task();
        task.setId(TestConstants.TEST_TASK_ID);
        task.setBooking(createBooking());
        task.setAssignedEmployee(createEmployee());
        task.setTitle(TestConstants.TEST_TASK_TITLE);
        task.setDescription(TestConstants.TEST_TASK_DESCRIPTION);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setSubTasks(new ArrayList<>());
        task.setTaskNotes(new ArrayList<>());
        task.setTaskImages(new ArrayList<>());
        return task;
    }
    
    /**
     * Creates a Task with specific status
     */
    public static Task createTask(TaskStatus status) {
        Task task = createTask();
        task.setStatus(status);
        return task;
    }
    
    /**
     * Creates a Task assigned to a specific employee
     */
    public static Task createTask(User employee, Booking booking) {
        Task task = createTask();
        task.setAssignedEmployee(employee);
        task.setBooking(booking);
        return task;
    }

    // ===================================================================
    // VEHICLE BUILDERS
    // ===================================================================
    
    /**
     * Creates a basic Vehicle entity
     * 
     * LEARNING: Vehicles belong to customers
     * Used for tracking service history
     */
    public static Vehicle createVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(TestConstants.TEST_VEHICLE_ID);
        vehicle.setUser(createCustomer());
        vehicle.setType(TestConstants.TEST_VEHICLE_TYPE);
        vehicle.setMake(TestConstants.TEST_VEHICLE_MAKE);
        vehicle.setModel(TestConstants.TEST_VEHICLE_MODEL);
        vehicle.setYear(TestConstants.TEST_VEHICLE_YEAR);
        vehicle.setPlateNumber(TestConstants.TEST_PLATE_NUMBER);
        vehicle.setChassisNumber(TestConstants.TEST_CHASSIS_NUMBER);
        vehicle.setFuelType(TestConstants.TEST_FUEL_TYPE);
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        return vehicle;
    }
    
    /**
     * Creates a Vehicle for a specific user
     */
    public static Vehicle createVehicle(User user) {
        Vehicle vehicle = createVehicle();
        vehicle.setUser(user);
        return vehicle;
    }

    // Private constructor to prevent instantiation
    private TestDataBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
