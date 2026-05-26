package com.pm.authservice.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.authservice.dto.DoctorRequestDTO;
import com.pm.authservice.dto.DoctorResponseDTO;
import com.pm.authservice.model.Doctor;
import com.pm.authservice.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(doctor -> ResponseEntity.ok(toResponseDTO(doctor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorRequestDTO request) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(toResponseDTO(updatedDoctor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    private DoctorResponseDTO toResponseDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId().toString());
        dto.setName(doctor.getName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setUserId(doctor.getUserId().toString());
        return dto;
    }
}
