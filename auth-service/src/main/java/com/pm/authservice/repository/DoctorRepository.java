package com.pm.authservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pm.authservice.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByUserId(UUID userId);
}
