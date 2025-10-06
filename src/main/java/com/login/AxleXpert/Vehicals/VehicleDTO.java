package com.login.AxleXpert.Vehicals;

import java.time.LocalDate;

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

    public VehicleDTO() {}

    public VehicleDTO(Long id, String type, Integer year, String make, String model, String fuelType, String plateNumber, String chassisNumber, LocalDate lastServiceDate, Long userId) {
        this.id = id;
        this.type = type;
        this.year = year;
        this.make = make;
        this.model = model;
        this.fuelType = fuelType;
        this.plateNumber = plateNumber;
        this.chassisNumber = chassisNumber;
        this.lastServiceDate = lastServiceDate;
        this.userId = userId;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public static VehicleDTO fromEntity(Vehicle v) {
        if (v == null) return null;
        Long uid = v.getUser() != null ? v.getUser().getId() : null;
        return new VehicleDTO(v.getId(), v.getType(), v.getYear(), v.getMake(), v.getModel(), v.getFuelType(), v.getPlateNumber(), v.getChassisNumber(), v.getLastServiceDate(), uid);
    }
}
