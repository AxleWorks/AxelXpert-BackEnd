package com.login.AxleXpert.auth.controller;

import com.login.AxleXpert.auth.LoginResponse;
import com.login.AxleXpert.auth.dto.LoginDTO;
import com.login.AxleXpert.auth.dto.SignupDTO;
import com.login.AxleXpert.auth.dto.UserDTO_Auth;
import com.login.AxleXpert.auth.dto.PasswordResetConfirmDTO;
import com.login.AxleXpert.auth.dto.PasswordResetRequestDTO;
import com.login.AxleXpert.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDTO dto) {
        try {
            String activationLink = authService.registerUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
            // In production, you would send the activationLink to the user's email. For now return it so frontend/test can use it.
            return ResponseEntity.ok("User created. Activation link: " + activationLink);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Email already registered") || msg.contains("Username already taken"))) {
                return ResponseEntity.status(409).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String token) {
        boolean ok = authService.activateUser(token);
        if (ok) return ResponseEntity.ok("Account activated");
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        String result = authService.loginUser(dto.getEmail(), dto.getPassword());
        switch (result) {
            case "OK":
                // Build response with id, username, email and role (don't include password)
                UserDTO_Auth u = authService.getUserByEmail(dto.getEmail());
                if (u == null) return ResponseEntity.status(500).body("Unexpected error: user not found");
                LoginResponse resp = new LoginResponse(u.getJWTToken());
                return ResponseEntity.ok(resp);
            case "NOT_FOUND":
                return ResponseEntity.status(404).body("User not found");
            case "INVALID":
                return ResponseEntity.status(401).body("Invalid email or password");
            case "BLOCKED":
                return ResponseEntity.status(403).body("User is blocked");
            case "INACTIVE":
                return ResponseEntity.status(403).body("Account not activated please check your email");
            default:
                return ResponseEntity.status(500).body("Unknown error");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok("Authentication service is running");
    }

    // TEMPORARY DEBUG ENDPOINT - Remove after testing
    @GetMapping("/debug-password")
    public ResponseEntity<?> debugPassword(@RequestParam String password) {
        org.springframework.security.crypto.password.PasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        
        String hash = encoder.encode(password);
        
        return ResponseEntity.ok(
            "Password: " + password + "\n" +
            "BCrypt Hash: " + hash + "\n" +
            "SQL: UPDATE user SET password = '" + hash + "' WHERE password = '" + password + "';"
        );
    }

    // Simple endpoint to test SMTP connectivity from the running app. Call with ?to=you@example.com
    @GetMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestParam(required = false) String to) {
        String email = (to == null || to.isBlank()) ? "test@example.com" : to;
        try {
            String res = authService.sendTestEmail(email);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Request password reset - sends email with reset link
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequestDTO request) {
        try {
            String message = authService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset password using token from email
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetConfirmDTO request) {
        try {
            String message = authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validate reset token (optional - for frontend to check if token is valid)
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            // You can add validation logic in AuthService if needed
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}
