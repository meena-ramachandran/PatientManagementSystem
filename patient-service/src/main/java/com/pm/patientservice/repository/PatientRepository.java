package com.pm.patientservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.patientservice.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<Patient> findByNameContainingIgnoreCase(String name);

    List<Patient> findByEmailContainingIgnoreCase(String email);

    List<Patient> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
