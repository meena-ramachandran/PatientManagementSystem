package com.pm.patientservice.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

class PatientMapperTest {

    @Test
    void toPatientResponseDTOMapsAllFields() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("Alice");
        patient.setEmail("alice@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.of(2024, 1, 1));

        PatientResponseDTO dto = PatientMapper.toPatientResponseDTO(patient);

        assertEquals(patient.getId().toString(), dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals("1990-01-01", dto.getDateOfBirth());
    }

    @Test
    void toPatientMapsAllFields() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Bob");
        request.setEmail("bob@example.com");
        request.setAddress("456 Oak Ave");
        request.setDateOfBirth("1985-05-05");
        request.setRegisteredDate("2024-04-01");

        Patient patient = PatientMapper.toPatient(request);

        assertEquals("Bob", patient.getName());
        assertEquals("bob@example.com", patient.getEmail());
        assertEquals("456 Oak Ave", patient.getAddress());
        assertEquals(LocalDate.of(1985, 5, 5), patient.getDateOfBirth());
        assertEquals(LocalDate.of(2024, 4, 1), patient.getRegisteredDate());
    }
}
