package com.login.AxleXpert.Services.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Services.repository.ServiceRepository;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceRepository repository;

    @Autowired
    public ServiceController(ServiceRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public ResponseEntity<List<ServiceDTO>> getAll() {
        List<ServiceDTO> dtos = repository.findAll()
                .stream()
                .map(ServiceDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
