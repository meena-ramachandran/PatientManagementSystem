package com.pm.appointmentservice.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.model.Appointment;

public class AppointmentMapper {

    public static Appointment toAppointment(AppointmentRequestDTO request) {
        Appointment appointment = new Appointment();
        appointment.setPatientId(UUID.fromString(request.getPatientId()));
        appointment.setUserId(UUID.fromString(request.getUserId()));
        appointment.setAppointmentDateTime(LocalDateTime.parse(request.getAppointmentDateTime()));
        appointment.setStatus(request.getStatus());
        appointment.setAppointmentFee(request.getAppointmentFee() != null ? request.getAppointmentFee() : java.math.BigDecimal.ZERO);
        appointment.setNotes(request.getNotes());
        return appointment;
    }

    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setId(appointment.getId().toString());
        response.setPatientId(appointment.getPatientId().toString());
        response.setUserId(appointment.getUserId().toString());
        response.setBillingAccountId(appointment.getBillingAccountId());
        response.setAppointmentFee(appointment.getAppointmentFee());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime().toString());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setCreatedAt(appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null);
        response.setUpdatedAt(appointment.getUpdatedAt() != null ? appointment.getUpdatedAt().toString() : null);
        return response;
    }
}
