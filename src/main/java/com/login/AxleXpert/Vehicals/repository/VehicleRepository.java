package com.login.AxleXpert.Vehicals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Vehicals.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser_Id(Long userId);
    Optional<Vehicle> findByPlateNumber(String plateNumber);
}
