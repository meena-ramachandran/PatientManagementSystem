package com.pm.amalyticsservice.dto;

import jakarta.validation.constraints.NotBlank;

public class AnalyticsEventRequestDTO {

    @NotBlank
    private String patientId;

    @NotBlank
    private String eventType;

    private String details;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
