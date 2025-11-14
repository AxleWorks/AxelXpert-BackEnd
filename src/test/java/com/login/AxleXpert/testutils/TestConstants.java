package com.login.AxleXpert.testutils;

/**
 * Test Constants - Centralized values for testing
 * 
 * LEARNING: Why constants?
 * - Consistency: All tests use the same values
 * - Easy updates: Change once, applies everywhere
 * - Readability: Named constants are clearer than magic numbers
 */
public class TestConstants {
    
    // ===================================================================
    // User Test Data
    // ===================================================================
    public static final Long TEST_USER_ID = 1L;
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "Password123!";
    public static final String TEST_ENCRYPTED_PASSWORD = "$2a$10$XQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW";
    public static final String TEST_PHONE = "555-1234";
    public static final String TEST_ADDRESS = "123 Test Street";
    
    // User Roles
    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_EMPLOYEE = "employee";
    public static final String ROLE_MANAGER = "manager";
    
    // ===================================================================
    // Service Test Data
    // ===================================================================
    public static final Long TEST_SERVICE_ID = 1L;
    public static final String TEST_SERVICE_NAME = "Oil Change";
    public static final String TEST_SERVICE_PRICE = "29.99";
    public static final Integer TEST_SERVICE_DURATION = 30; // minutes
    
    // ===================================================================
    // Branch Test Data
    // ===================================================================
    public static final Long TEST_BRANCH_ID = 1L;
    public static final String TEST_BRANCH_NAME = "Downtown Branch";
    public static final String TEST_BRANCH_ADDRESS = "456 Main Street";
    public static final String TEST_BRANCH_PHONE = "555-5000";
    public static final String TEST_BRANCH_EMAIL = "downtown@axlexpert.com";
    
    // ===================================================================
    // Booking Test Data
    // ===================================================================
    public static final Long TEST_BOOKING_ID = 1L;
    public static final String TEST_CUSTOMER_NAME = "John Doe";
    public static final String TEST_VEHICLE = "Toyota Camry 2020";
    
    // ===================================================================
    // Task Test Data
    // ===================================================================
    public static final Long TEST_TASK_ID = 1L;
    public static final String TEST_TASK_TITLE = "Change Engine Oil";
    public static final String TEST_TASK_DESCRIPTION = "Replace old oil with synthetic blend";
    
    // ===================================================================
    // Vehicle Test Data
    // ===================================================================
    public static final Long TEST_VEHICLE_ID = 1L;
    public static final String TEST_VEHICLE_TYPE = "Car";
    public static final String TEST_VEHICLE_MAKE = "Toyota";
    public static final String TEST_VEHICLE_MODEL = "Camry";
    public static final Integer TEST_VEHICLE_YEAR = 2020;
    public static final String TEST_PLATE_NUMBER = "ABC123";
    public static final String TEST_CHASSIS_NUMBER = "1HGBH41JXMN109186";
    public static final String TEST_FUEL_TYPE = "Petrol";
    
    // ===================================================================
    // JWT Test Data
    // ===================================================================
    public static final String TEST_JWT_SECRET = "testSecretKeyForJUnitTestsOnly12345678901234567890123456789012345678901234567890";
    public static final long TEST_JWT_EXPIRATION = 3600000L; // 1 hour in milliseconds
    
    // ===================================================================
    // Activation Token Test Data
    // ===================================================================
    public static final String TEST_ACTIVATION_TOKEN = "test-activation-token-12345";
    public static final String TEST_RESET_TOKEN = "test-reset-token-67890";
    
    // ===================================================================
    // Pagination Test Data
    // ===================================================================
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    
    // Private constructor to prevent instantiation
    private TestConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
