package com.login.AxleXpert.Branches.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.AxleXpert.Branches.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByName(String name);
}
