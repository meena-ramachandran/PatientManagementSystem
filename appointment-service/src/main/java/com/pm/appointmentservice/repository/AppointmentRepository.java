package com.pm.appointmentservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pm.appointmentservice.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
}
