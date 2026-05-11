package com.pm.amalyticsservice.dto;

public class AnalyticsSummaryDTO {

    private String eventType;
    private String patientId;
    private long eventCount;
    private int uniquePatientCount;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public int getUniquePatientCount() {
        return uniquePatientCount;
    }

    public void setUniquePatientCount(int uniquePatientCount) {
        this.uniquePatientCount = uniquePatientCount;
    }
}
