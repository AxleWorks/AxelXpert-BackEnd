package com.login.AxleXpert.Services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Services.entity.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    java.util.Optional<Service> findByNameIgnoreCase(String name);
}
