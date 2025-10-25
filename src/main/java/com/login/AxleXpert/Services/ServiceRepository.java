package com.login.AxleXpert.Services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
	java.util.Optional<Service> findByNameIgnoreCase(String name);
}
