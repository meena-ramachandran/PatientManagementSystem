package com.pm.appointmentservice.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pm.appointmentservice.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    boolean existsByPatientIdAndAppointmentDateTime(UUID patientId, LocalDateTime appointmentDateTime);

    boolean existsByUserIdAndAppointmentDateTime(UUID userId, LocalDateTime appointmentDateTime);

    boolean existsByPatientIdAndAppointmentDateTimeAndIdNot(UUID patientId, LocalDateTime appointmentDateTime, UUID id);

    boolean existsByUserIdAndAppointmentDateTimeAndIdNot(UUID userId, LocalDateTime appointmentDateTime, UUID id);

    java.util.List<Appointment> findByPatientId(UUID patientId);

    java.util.List<Appointment> findByUserId(UUID userId);

    java.util.List<Appointment> findByUserIdAndAppointmentDateTimeBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    java.util.List<Appointment> findByPatientIdAndAppointmentDateTimeBetween(UUID patientId, LocalDateTime start, LocalDateTime end);

    java.util.List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}

