package com.login.AxleXpert.Vehicals;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        List<VehicleDTO> dtos = vehicleRepository.findAll().stream().map(VehicleDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDTO>> getByUser(@PathVariable Long userId) {
        List<VehicleDTO> dtos = vehicleRepository.findByUser_Id(userId).stream().map(VehicleDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
