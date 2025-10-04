package com.login.AxleXpert.checkstatus;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

    public String health(){
            return "healthy";
    }

    public String greeting(){
        return "welcome to the Axlexpert";
    }

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String username;

        @Column(nullable = false)
        private String password;

    }
}
