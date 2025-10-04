package com.login.AxleXpert.auth;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.activation.base-url:}")
    private String activationBaseUrl;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final SecureRandom secureRandom = new SecureRandom();

    private String generateToken32() {
        byte[] bytes = new byte[24]; // 24 bytes -> 32 chars when base64-url encoded without padding
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        if (token.length() > 32) return token.substring(0, 32);
        while (token.length() < 32) token += "0";
        return token;
    }

    // Register user and return activation link (should be emailed in production)
    public String registerUser(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setIs_Active(false);
        user.setIs_Blocked(false);
        user.setRole("USER");
        String token = generateToken32();
        user.setToken(token);

        userRepository.save(user);

    // Build activation link; if activationBaseUrl is configured, produce absolute URL
    String path = String.format("http://localhost:8080/api/auth/activate?token=%s", token);
    String activationLink = (activationBaseUrl != null && !activationBaseUrl.isBlank())
        ? activationBaseUrl.replaceAll("/+$", "") + path
        : path;

        // Try to send activation email if mailSender is configured
        if (mailSender != null && email != null && !email.isBlank()) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(email);
                msg.setSubject("Activate your AxleXpert account");
                msg.setText("Hello " + username + ",\n\nPlease activate your account by clicking the link below:\n" + activationLink + "\n\nIf you didn't request this, ignore this email.");
                mailSender.send(msg);
            } catch (Exception e) {
                // Log sending failure and continue returning the link so caller can display it for testing
                log.warn("Failed to send activation email to {}: {}", email, e.getMessage());
            }
        }

        return activationLink;
    }

    // Activate by token
    public boolean activateUser(String token) {
        return userRepository.findByToken(token).map(user -> {
            user.setIs_Active(true);
            user.setToken(null);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    // Return status codes so controller can decide response
    public String loginUser(String email, String password) {
        return userRepository.findByEmail(email).map(user -> {
            if (Boolean.TRUE.equals(user.getIs_Blocked())) return "BLOCKED";
            if (Boolean.FALSE.equals(user.getIs_Active())) return "INACTIVE";
            if (!user.getPassword().equals(password)) return "INVALID";
            return "OK";
        }).orElse("NOT_FOUND");
    }
    
    // Return the User entity for a given username (caller must avoid exposing sensitive fields)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
