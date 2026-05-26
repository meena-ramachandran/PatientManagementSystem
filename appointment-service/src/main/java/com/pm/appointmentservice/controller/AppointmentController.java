package com.pm.appointmentservice.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments(
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID patientId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String date) {
        return ResponseEntity.ok(appointmentService.getAppointments(patientId, userId, date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable UUID id,
            @Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
