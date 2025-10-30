package com.login.AxleXpert.auth;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;
import com.login.AxleXpert.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ CRITICAL: Must be autowired

    @Value("${app.activation.base-url:http://localhost:8080}")
    private String activationBaseUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user with encrypted password and send activation email
     */
    public String registerUser(String username, String password, String email) {
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        
        // ✅ HASH PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(password));
        
        user.setIs_Active(false);  // Require activation
        user.setIs_Blocked(false);
        user.setRole("user");
        String token = generateToken32();
        user.setToken(token);

        userRepository.save(user);
        log.info("User registered: {}", email);

        // Build activation link
        String activationLink = activationBaseUrl + "/api/auth/activate?token=" + token;
        
        // ✅ SEND ACTIVATION EMAIL
        if (mailSender != null) {
            try {
                sendActivationEmail(email, username, activationLink);
                log.info("Activation email sent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send activation email to {}: {}", email, e.getMessage());
                // Don't fail registration if email fails
            }
        } else {
            log.warn("Mail sender not configured. Activation email not sent.");
        }

        return activationLink;
    }

    /**
     * Send activation email
     */
    private void sendActivationEmail(String toEmail, String username, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("AxleXpert - Activate Your Account");
        message.setText(
            "Hello " + username + ",\n\n" +
            "Welcome to AxleXpert! Please activate your account by clicking the link below:\n\n" +
            activationLink + "\n\n" +
            "This link will expire after use.\n\n" +
            "If you did not create this account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "AxleXpert Team"
        );
        
        mailSender.send(message);
    }

    /**
     * Login user with password verification
     */
    public String loginUser(String emailOrUsername, String password) {
        // Try to find by email first
        Optional<User> userOpt = userRepository.findByEmail(emailOrUsername);
        
        // If not found by email, try username
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(emailOrUsername);
        }
        
        // User doesn't exist
        if (userOpt.isEmpty()) {
            log.warn("Login attempt for non-existent user: {}", emailOrUsername);
            return "NOT_FOUND";
        }
        
        User user = userOpt.get();
        
        // ✅ VERIFY PASSWORD USING ENCODER
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", emailOrUsername);
            return "INVALID";
        }
        
        // Check if user is blocked
        if (user.getIs_Blocked() != null && user.getIs_Blocked()) {
            log.warn("Blocked user login attempt: {}", emailOrUsername);
            return "BLOCKED";
        }
        
        // Check if user is active
        if (user.getIs_Active() == null || !user.getIs_Active()) {
            log.warn("Inactive user login attempt: {}", emailOrUsername);
            return "INACTIVE";
        }
        
        log.info("Successful login: {}", emailOrUsername);
        return "OK";
    }

    /**
     * Get user details after successful login
     */
    public UserDTO_Auth getUserByEmail(String emailOrUsername) {
        Optional<User> userOpt = userRepository.findByEmail(emailOrUsername);
        
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(emailOrUsername);
        }
        
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        
        // Build DTO without password
        UserDTO_Auth dto = new UserDTO_Auth();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsBlocked(user.getIs_Blocked());
        dto.setIsActive(user.getIs_Active());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        dto.setJWTToken(token);
        
        return dto;
    }

    /**
     * Activate user account
     */
    public boolean activateUser(String token) {
        Optional<User> userOpt = userRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            log.warn("Invalid activation token: {}", token);
            return false;
        }
        
        User user = userOpt.get();
        user.setIs_Active(true);
        user.setToken(null);  // Clear token after use
        userRepository.save(user);
        
        log.info("User activated: {}", user.getEmail());
        return true;
    }

    /**
     * Generate secure random token
     */
    private String generateToken32() {
        byte[] bytes = new byte[24]; // 24 bytes -> 32 chars base64
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Send test email - for testing SMTP configuration
     */
    public String sendTestEmail(String to) {
        if (mailSender == null) {
            throw new RuntimeException("Mail sender not configured. Check MAIL_PASSWORD environment variable.");
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("AxleXpert - Test Email");
            message.setText("This is a test email from AxleXpert backend.\n\nEmail configuration is working correctly!");
            
            mailSender.send(message);
            log.info("Test email sent to: {}", to);
            return "Test email sent successfully to: " + to;
        } catch (Exception e) {
            log.error("Failed to send test email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Resend activation email
     */
    public String resendActivationEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        if (user.getIs_Active()) {
            throw new RuntimeException("User already activated");
        }
        
        // Generate new token
        String token = generateToken32();
        user.setToken(token);
        userRepository.save(user);
        
        String activationLink = activationBaseUrl + "/api/auth/activate?token=" + token;
        
        if (mailSender != null) {
            try {
                sendActivationEmail(email, user.getUsername(), activationLink);
                log.info("Activation email resent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to resend activation email: {}", e.getMessage());
                throw new RuntimeException("Failed to send email: " + e.getMessage());
            }
        }
        
        return activationLink;
    }
}
