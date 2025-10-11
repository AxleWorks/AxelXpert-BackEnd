package com.login.AxleXpert.Vehicals;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        List<VehicleDTO> dtos = vehicleService.getAllVehicles();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDTO>> getByUser(@PathVariable Long userId) {
        List<VehicleDTO> dtos = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(dtos);
    }
}
