package com.login.AxleXpert.Vehicals.dto.;

import java.time.LocalDate;

import com.login.AxleXpert.Vehicals.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String type;
    private Integer year;
    private String make;
    private String model;
    private String fuelType;
    private String plateNumber;
    private String chassisNumber;
    private LocalDate lastServiceDate;
    private Long userId;

    public static VehicleDTO fromEntity(Vehicle v) {
        if (v == null) return null;
        Long uid = v.getUser() != null ? v.getUser().getId() : null;
        return new VehicleDTO(v.getId(), v.getType(), v.getYear(), v.getMake(), v.getModel(), v.getFuelType(), v.getPlateNumber(), v.getChassisNumber(), v.getLastServiceDate(), uid);
    }
}
