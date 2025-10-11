package com.login.AxleXpert.Vehicals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUser_Id(userId)
                .stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<VehicleDTO> getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .map(VehicleDTO::fromEntity);
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle vehicle = new Vehicle();
        mapDtoToEntity(vehicleDTO, vehicle);
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return VehicleDTO.fromEntity(savedVehicle);
    }

    public Optional<VehicleDTO> updateVehicle(Long id, VehicleDTO vehicleDTO) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    mapDtoToEntity(vehicleDTO, vehicle);
                    Vehicle updatedVehicle = vehicleRepository.save(vehicle);
                    return VehicleDTO.fromEntity(updatedVehicle);
                });
    }

    public boolean deleteVehicle(Long id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void mapDtoToEntity(VehicleDTO dto, Vehicle entity) {
        entity.setType(dto.getType());
        entity.setYear(dto.getYear());
        entity.setMake(dto.getMake());
        entity.setModel(dto.getModel());
        entity.setFuelType(dto.getFuelType());
        entity.setPlateNumber(dto.getPlateNumber());
        entity.setChassisNumber(dto.getChassisNumber());
        entity.setLastServiceDate(dto.getLastServiceDate());
        
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(entity::setUser);
        }
    }
}