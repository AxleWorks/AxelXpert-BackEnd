package com.login.AxleXpert.checkstatus;

import com.login.AxleXpert.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class CheckController {

    @Autowired
    CheckService checkService;

    @GetMapping("/status")
    public ResponseEntity<?> status() {

        return ResponseEntity.ok("Authentication service is running");
    }

    @GetMapping("/health")
     public ResponseEntity<?> health() {
        return ResponseEntity.ok(checkService.health());
    }

@GetMapping("")
public ResponseEntity<?> greting() {
    return ResponseEntity.ok(checkService.greeting());
}

}
