package com.login.AxleXpert.auth;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;

import jakarta.mail.internet.MimeMessage;

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

    public String registerUser(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
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

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException dive) {
            // Translate DB constraint failures into a clear exception
            log.warn("Data integrity violation when saving user {}: {}", username, dive.getMessage());
            // If email is duplicate, give a friendly message; otherwise rethrow
            if (email != null && userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
            if (userRepository.findByUsername(username).isPresent()) {
                throw new RuntimeException("Username already taken");
            }
            throw dive;
        }

    String path = String.format("http://localhost:8080/api/auth/activate?token=%s", token);
    String activationLink = (activationBaseUrl != null && !activationBaseUrl.isBlank())
        ? activationBaseUrl.replaceAll("/+$", "") + path
        : path;
        // Send a styled HTML activation email with a plain-text fallback
        if (mailSender != null && email != null && !email.isBlank()) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                String html = "<!doctype html>" +
                        "<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
                        "<style>body{font-family: -apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,\"Helvetica Neue\",Arial,sans-serif;background:#f4f7fb;margin:0;padding:0;}" +
                        ".container{max-width:600px;margin:48px auto;background:#ffffff;border-radius:12px;box-shadow:0 6px 18px rgba(20,30,50,0.08);overflow:hidden;}" +
                        ".header{padding:28px 32px;background:linear-gradient(90deg,#0ea5b7,#2563eb);color:#fff;}" +
                        ".content{padding:32px;color:#0f172a;}" +
                        ".btn{display:inline-block;padding:14px 22px;background:#2563eb;color:#fff;border-radius:8px;text-decoration:none;font-weight:600;margin-top:18px;}" +
                        ".muted{color:#64748b;font-size:13px;margin-top:18px;}" +
                        "a.fallback{word-break:break-all;color:#2563eb;}" +
                        "</style></head><body>" +
                        "<div class=\"container\">" +
                        "<div class=\"header\"><h2 style=\"margin:0;font-size:20px\">Activate your AxleXpert account</h2></div>" +
                        "<div class=\"content\">" +
                        "<p style=\"font-size:15px;margin:0 0 12px 0\">Hello " + username + ",</p>" +
                        "<p style=\"margin:0 0 18px 0\">Click the button below to activate your account. The link will expire according to system policy.</p>" +
                        "<a class=\"btn\" href=\"" + activationLink + "\" style=\"color: #fff; text-decoration: none;\">Activate Account</a>" +
                        "<p class=\"muted\">If the button doesn't work, copy and paste the following link into your browser:</p>" +
                        "<p><a class=\"fallback\" href=\"" + activationLink + "\">" + activationLink + "</a></p>" +
                        "<p class=\"muted\">If you didn't request this, you can safely ignore this email.</p>" +
                        "</div></div></body></html>";

                helper.setTo(email);
                helper.setSubject("Activate your AxleXpert account");
                helper.setText(html, true);
                mailSender.send(mimeMessage);
            } catch (Exception e) {
                log.warn("Failed to send HTML activation email to {}: {}", email, e.getMessage());
                try {
                    SimpleMailMessage msg = new SimpleMailMessage();
                    msg.setTo(email);
                    msg.setSubject("Activate your AxleXpert account");
                    msg.setText("Hello " + username + ",\n\nPlease activate your account by clicking the link below:\n" + activationLink + "\n\nIf you didn't request this, ignore this email.");
                    mailSender.send(msg);
                } catch (Exception ex) {
                    log.warn("Failed to send fallback activation email to {}: {}", email, ex.getMessage());
                }
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
