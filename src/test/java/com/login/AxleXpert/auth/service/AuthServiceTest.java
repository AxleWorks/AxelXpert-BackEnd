package com.login.AxleXpert.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.auth.dto.UserDTO_Auth;
import com.login.AxleXpert.common.EmailService;
import com.login.AxleXpert.security.JwtUtil;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * ============================================================================
 * AuthService Unit Tests - Complete Test Suite
 * ============================================================================
 * 
 * LEARNING OBJECTIVES:
 * 1. Unit Testing: Testing a single class (AuthService) in isolation
 * 2. Mocking: Using Mockito to simulate dependencies
 * 3. Test Organization: Using @Nested classes for logical grouping
 * 4. AAA Pattern: Arrange, Act, Assert in every test
 * 5. AssertJ: Fluent assertion library for readable tests
 * 6. ReflectionTestUtils: Injecting mocks into private fields
 * 
 * MOCKITO BASICS:
 * - @Mock: Creates a mock object (fake version of a dependency)
 * - when().thenReturn(): Configures what a mock should return
 * - verify(): Checks that a method was called on a mock
 * 
 * REFLECTION TEST UTILS:
 * - Spring provides ReflectionTestUtils for setting private fields
 * - Useful when the class uses field injection (@Autowired on fields)
 * - setField(object, "fieldName", value): Injects value into field
 * 
 * Coverage Target: 95%+
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)  // Enables Mockito annotations
@DisplayName("AuthService Unit Tests")
public class AuthServiceTest {

    // ========================================================================
    // MOCKS - Fake versions of dependencies
    // ========================================================================
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    /**
     * LEARNING: Mock the JavaMailSender too
     * The AuthService checks if mailSender is not null before sending emails
     */
    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    /**
     * LEARNING: Instead of @InjectMocks, we create manually
     * because AuthService uses field injection
     */
    private AuthService authService;

    // ========================================================================
    // TEST DATA - Reusable test objects
    // ========================================================================
    
    private User testUser;
    private User inactiveUser;
    private User blockedUser;
    
    /**
     * LEARNING: @BeforeEach runs before EVERY test method
     * 
     * KEY CHANGE: We use ReflectionTestUtils to inject mocks
     * into the private fields that use @Autowired
     */
    @BeforeEach
    void setUp() {
        // Create AuthService with constructor dependencies
        authService = new AuthService(userRepository, jwtUtil);
        
        /**
         * LEARNING: ReflectionTestUtils.setField()
         * 
         * Why? AuthService has @Autowired fields:
         *   @Autowired private PasswordEncoder passwordEncoder;
         *   @Autowired private EmailService emailService;
         *   @Autowired private JavaMailSender mailSender;
         * 
         * These aren't injected through constructor, so we inject them manually
         */
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "emailService", emailService);
        ReflectionTestUtils.setField(authService, "mailSender", mailSender);
        
        // Create test users using our TestDataBuilder utility
        testUser = TestDataBuilder.createUser();
        inactiveUser = TestDataBuilder.createInactiveUser();
        blockedUser = TestDataBuilder.createBlockedUser();
    }

    // ========================================================================
    // USER REGISTRATION TESTS
    // ========================================================================
    
    /**
     * LEARNING: @Nested classes group related tests together
     * Benefits:
     * - Better organization in test reports
     * - Can have separate @BeforeEach for each group
     * - Easier to read and maintain
     */
    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {
        
        /**
         * HAPPY PATH TEST
         * 
         * LEARNING: The AAA Pattern (Arrange-Act-Assert)
         * - ARRANGE: Set up test data and mock behavior
         * - ACT: Call the method being tested
         * - ASSERT: Verify the result is correct
         */
        @Test
        @DisplayName("Should successfully register a new user with valid data")
        void shouldRegisterUser_whenValidDataProvided() {
            // ============================================================
            // ARRANGE - Set up the test scenario
            // ============================================================
            
            String username = "newuser";
            String password = "Password123!";
            String email = "newuser@example.com";
            String encodedPassword = "encoded_password_hash";
            
            /**
             * LEARNING: when().thenReturn() configures mock behavior
             * 
             * This says: "When someone calls findByEmail() with this email,
             * return Optional.empty() (meaning no existing user)"
             */
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
            
            /**
             * LEARNING: when().thenReturn() for username check
             */
            when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());
            
            /**
             * LEARNING: Simulate password encoding
             * When encode() is called with any string, return our fake hash
             */
            when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
            
            /**
             * LEARNING: any(User.class) is an argument matcher
             * It means "accept any User object"
             * We use this when we don't care about exact values
             */
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L); // Simulate database generating an ID
                    return user;
                });
            
            /**
             * LEARNING: doNothing() for void methods
             * 
             * JavaMailSender.send() returns void, so we use doNothing()
             * This tells Mockito: "When send() is called, just do nothing"
             */
            doNothing().when(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
            
            // ============================================================
            // ACT - Call the method we're testing
            // ============================================================
            
            String activationLink = authService.registerUser(username, password, email);
            
            // ============================================================
            // ASSERT - Verify the results
            // ============================================================
            
            /**
             * LEARNING: AssertJ assertions are readable and chainable
             * 
             * assertThat(value) starts the assertion
             * .isNotNull() checks it's not null
             * .contains("activate") checks it contains this text
             */
            assertThat(activationLink)
                .isNotNull()
                .contains("activate")
                .contains("token=");
            
            /**
             * LEARNING: verify() checks that methods were called
             * 
             * This verifies that save() was called exactly once
             * with any User object
             * 
             * Why verify? To ensure the user was actually saved!
             */
            verify(userRepository, times(1)).save(any(User.class));
            
            /**
             * LEARNING: verify with argument matchers
             * Checks that an email was sent via mailSender
             */
            verify(mailSender, times(1))
                .send(any(org.springframework.mail.SimpleMailMessage.class));
        }
        
        /**
         * EXCEPTION TEST
         * 
         * LEARNING: Testing failure scenarios is as important as testing success!
         * This tests that duplicate emails are properly rejected
         */
        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowException_whenEmailAlreadyExists() {
            // ARRANGE
            String existingEmail = testUser.getEmail();
            
            /**
             * LEARNING: Simulate that a user with this email already exists
             */
            when(userRepository.findByEmail(existingEmail))
                .thenReturn(Optional.of(testUser));
            
            // ACT & ASSERT
            /**
             * LEARNING: assertThatThrownBy() tests exceptions
             * 
             * The lambda () -> {...} is executed, and we check:
             * - Is the right exception thrown?
             * - Does it have the right message?
             */
            assertThatThrownBy(() -> 
                authService.registerUser("anyuser", "anypass", existingEmail)
            )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");
            
            /**
             * LEARNING: verify with never()
             * Ensures save() was NEVER called (because registration failed)
             */
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw exception when username already taken")
        void shouldThrowException_whenUsernameAlreadyTaken() {
            // ARRANGE
            when(userRepository.findByEmail("new@example.com"))
                .thenReturn(Optional.empty());
            when(userRepository.findByUsername(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));
            
            // ACT & ASSERT
            assertThatThrownBy(() -> 
                authService.registerUser(testUser.getUsername(), "pass", "new@example.com")
            )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already taken");
            
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // ========================================================================
    // USER ACTIVATION TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("User Activation Tests")
    class ActivationTests {
        
        @Test
        @DisplayName("Should activate user with valid token")
        void shouldActivateUser_whenValidTokenProvided() {
            // ARRANGE
            String validToken = inactiveUser.getToken();
            
            when(userRepository.findByToken(validToken))
                .thenReturn(Optional.of(inactiveUser));
            when(userRepository.save(any(User.class)))
                .thenReturn(inactiveUser);
            
            // ACT
            boolean result = authService.activateUser(validToken);
            
            // ASSERT
            assertThat(result).isTrue();
            assertThat(inactiveUser.getIs_Active()).isTrue();
            assertThat(inactiveUser.getToken()).isNull();
            
            verify(userRepository, times(1)).save(inactiveUser);
        }
        
        @Test
        @DisplayName("Should return false when token is invalid")
        void shouldReturnFalse_whenTokenIsInvalid() {
            // ARRANGE
            when(userRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());
            
            // ACT
            boolean result = authService.activateUser("invalid-token");
            
            // ASSERT
            assertThat(result).isFalse();
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should return false when token is null")
        void shouldReturnFalse_whenTokenIsNull() {
            // ARRANGE
            /**
             * LEARNING: When token is null, findByToken will be called with null
             * We need to mock this scenario
             */
            when(userRepository.findByToken(null))
                .thenReturn(Optional.empty());
            
            // ACT
            boolean result = authService.activateUser(null);
            
            // ASSERT
            assertThat(result).isFalse();
            /**
             * LEARNING: The method WILL call findByToken (with null)
             * so we verify it was called, not that it was never called
             */
            verify(userRepository, times(1)).findByToken(null);
        }
    }

    // ========================================================================
    // USER LOGIN TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("User Login Tests")
    class LoginTests {
        
        @Test
        @DisplayName("Should successfully login with valid credentials")
        void shouldLogin_whenCredentialsAreValid() {
            // ARRANGE
            String email = testUser.getEmail();
            String password = "Password123!";
            
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(password, testUser.getPassword()))
                .thenReturn(true);
            
            // ACT
            String result = authService.loginUser(email, password);
            
            // ASSERT
            assertThat(result).isEqualTo("OK");
        }
        
        @Test
        @DisplayName("Should return NOT_FOUND when user does not exist")
        void shouldReturnNotFound_whenUserDoesNotExist() {
            // ARRANGE
            when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
            
            // ACT
            String result = authService.loginUser("nonexistent@example.com", "anypass");
            
            // ASSERT
            assertThat(result).isEqualTo("NOT_FOUND");
        }
        
        @Test
        @DisplayName("Should return INVALID when password is wrong")
        void shouldReturnInvalid_whenPasswordIsWrong() {
            // ARRANGE
            when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpass", testUser.getPassword()))
                .thenReturn(false);
            
            // ACT
            String result = authService.loginUser(testUser.getEmail(), "wrongpass");
            
            // ASSERT
            assertThat(result).isEqualTo("INVALID");
        }
        
        @Test
        @DisplayName("Should return BLOCKED when user is blocked")
        void shouldReturnBlocked_whenUserIsBlocked() {
            // ARRANGE
            when(userRepository.findByEmail(blockedUser.getEmail()))
                .thenReturn(Optional.of(blockedUser));
            when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
            
            // ACT
            String result = authService.loginUser(blockedUser.getEmail(), "anypass");
            
            // ASSERT
            assertThat(result).isEqualTo("BLOCKED");
        }
        
        @Test
        @DisplayName("Should return INACTIVE when account not activated")
        void shouldReturnInactive_whenAccountNotActivated() {
            // ARRANGE
            when(userRepository.findByEmail(inactiveUser.getEmail()))
                .thenReturn(Optional.of(inactiveUser));
            when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
            
            // ACT
            String result = authService.loginUser(inactiveUser.getEmail(), "anypass");
            
            // ASSERT
            assertThat(result).isEqualTo("INACTIVE");
        }
    }

    // ========================================================================
    // GET USER BY EMAIL TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Get User By Email Tests")
    class GetUserTests {
        
        @Test
        @DisplayName("Should return user DTO with JWT when user exists")
        void shouldReturnUserDTO_whenUserExists() {
            // ARRANGE
            String email = testUser.getEmail();
            String mockJwt = "mock.jwt.token";
            
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken(any(User.class)))
                .thenReturn(mockJwt);
            
            // ACT
            UserDTO_Auth result = authService.getUserByEmail(email);
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUser.getId());
            assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
            assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(result.getRole()).isEqualTo(testUser.getRole());
            assertThat(result.getJWTToken()).isEqualTo(mockJwt);
            
            verify(jwtUtil, times(1))
                .generateToken(any(User.class));
        }
        
        @Test
        @DisplayName("Should return null when user not found")
        void shouldReturnNull_whenUserNotFound() {
            // ARRANGE
            when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
            
            // ACT
            UserDTO_Auth result = authService.getUserByEmail("nonexistent@example.com");
            
            // ASSERT
            assertThat(result).isNull();
            verify(jwtUtil, never()).generateToken(any(User.class));
        }
    }

    /**
     * ========================================================================
     * SUMMARY OF TESTING CONCEPTS DEMONSTRATED
     * ========================================================================
     * 
     * 1. MOCKING:
     *    - Created fake versions of dependencies using @Mock
     *    - Configured mock behavior with when().thenReturn()
     *    - Verified method calls with verify()
     * 
     * 2. TEST ORGANIZATION:
     *    - Used @Nested classes to group related tests
     *    - Used @DisplayName for readable test names
     *    - Used @BeforeEach to set up common data
     * 
     * 3. AAA PATTERN:
     *    - Arrange: Set up test data and mock behavior
     *    - Act: Call the method being tested
     *    - Assert: Verify results and interactions
     * 
     * 4. ASSERTJ:
     *    - Fluent, readable assertions
     *    - Multiple checks in one statement
     *    - Clear error messages when tests fail
     * 
     * 5. COVERAGE:
     *    - Happy path (success cases)
     *    - Edge cases (null, empty)
     *    - Exception cases (errors)
     *    - Different user states (blocked, inactive, active)
     * 
     * NEXT STEPS:
     * - Run these tests with: mvn test
     * - Check coverage report: target/site/jacoco/index.html
     * - Apply same patterns to other services
     * ========================================================================
     */
}
