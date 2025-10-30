package com.login.AxleXpert.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDTO dto) {
        try {
            String activationLink = userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
            // In production you would send the activationLink to the user's email. For now return it so frontend/test can use it.
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
        boolean ok = userService.activateUser(token);
        if (ok) return ResponseEntity.ok("Account activated");
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        String result = userService.loginUser(dto.getEmail(), dto.getPassword());
        switch (result) {
            case "OK":
                // Build response with id, username, email, role, branchId, and branchName (don't include password)
                com.login.AxleXpert.Users.User u = userService.getUserByEmail(dto.getEmail());
                if (u == null) return ResponseEntity.status(500).body("Unexpected error: user not found");
                
                Long branchId = null;
                String branchName = null;
                if (u.getBranch() != null) {
                    branchId = u.getBranch().getId();
                    branchName = u.getBranch().getName();
                }
                
                LoginResponse resp = new LoginResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), branchId, branchName);
                return ResponseEntity.ok(resp);
            case "NOT_FOUND":
                return ResponseEntity.status(404).body("User not found");
            case "INVALID":
                return ResponseEntity.status(401).body("Invalid email or password");
            case "BLOCKED":
                return ResponseEntity.status(403).body("User is blocked");
            case "INACTIVE":
                return ResponseEntity.status(403).body("Account not activated");
            default:
                return ResponseEntity.status(500).body("Unknown error");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok("Authentication service is running");
    }

    // Simple endpoint to test SMTP connectivity from the running app. Call with ?to=you@example.com
    @GetMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestParam(required = false) String to) {
        String email = (to == null || to.isBlank()) ? "test@example.com" : to;
        try {
            String res = userService.sendTestEmail(email);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
