package com.login.AxleXpert.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam String username, @RequestParam String password) {
        try {
            return ResponseEntity.ok(userService.registerUser(username, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        boolean success = userService.loginUser(username, password);
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok("Authentication service is running");
    }

}
