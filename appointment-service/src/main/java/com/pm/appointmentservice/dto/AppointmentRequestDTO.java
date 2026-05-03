package com.pm.appointmentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AppointmentRequestDTO {

    @NotBlank
    private String patientId;

    @NotBlank
    private String userId;

    @NotBlank
    private String appointmentDateTime;

    @NotBlank
    private String status;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal appointmentFee;

    private String notes;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.math.BigDecimal getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(java.math.BigDecimal appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
