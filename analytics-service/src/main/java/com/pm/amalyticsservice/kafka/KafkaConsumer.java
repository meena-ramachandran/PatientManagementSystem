package com.pm.amalyticsservice.kafka;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import appointment.events.AppointmentEvent;
import patient.events.PatientEvent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.service.AnalyticsEventService;

import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final AnalyticsEventService analyticsEventService;

    public KafkaConsumer(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(ByteString.copyFrom(event));

            AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
            request.setPatientId(patientEvent.getPatientId());
            request.setEventType(patientEvent.getEventType());
            request.setDetails("name=" + patientEvent.getName() + ", email=" + patientEvent.getEmail());

            var saved = analyticsEventService.createEvent(request);
            log.info("Persisted analytics event with id={} from Kafka message", saved.getId());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing patient event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment", groupId = "analytics-service")
    public void consumeAppointmentEvent(byte[] event) {
        try {
            AppointmentEvent appointmentEvent = AppointmentEvent.parseFrom(ByteString.copyFrom(event));

            AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
            request.setPatientId(appointmentEvent.getPatientId());
            request.setEventType(appointmentEvent.getEventType());
            request.setDetails("appointmentId=" + appointmentEvent.getAppointmentId() + ", userId="
                    + appointmentEvent.getUserId() + ", status=" + appointmentEvent.getStatus());

            var saved = analyticsEventService.createEvent(request);
            log.info("Persisted analytics appointment event with id={} from Kafka message", saved.getId());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing appointment event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "billing", groupId = "analytics-service")
    public void consumeBillingEvent(byte[] event) {
        try {
            String payload = new String(event, StandardCharsets.UTF_8);
            String patientId = extractJsonField(payload, "patientId");
            String eventType = extractJsonField(payload, "eventType");
            String details = extractJsonField(payload, "details");

            AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
            request.setPatientId(patientId != null ? patientId : "");
            request.setEventType(eventType != null ? eventType : "BILLING_EVENT");
            request.setDetails(details != null ? details : payload);

            var saved = analyticsEventService.createEvent(request);
            log.info("Persisted analytics billing event with id={} from Kafka message", saved.getId());
        } catch (Exception e) {
            log.error("Error processing billing event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "patient-events")
    public void consume(
            ConsumerRecord<String, byte[]> record) {

        Header header = record.headers()
                .lastHeader("X-Correlation-Id");

        if (header != null) {
            String correlationId = new String(
                    header.value(),
                    StandardCharsets.UTF_8);
            MDC.put("correlationId", correlationId);
        }

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(
                    record.value());
            log.info(
                    "Received patient event for patientId={}",
                    patientEvent.getPatientId());
        } catch (InvalidProtocolBufferException e) {
            log.error(
                    "Failed to deserialize protobuf message",
                    e);
        } finally {
            MDC.clear();
        }
    }

    private String extractJsonField(String payload, String fieldName) {
        Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
        Matcher matcher = pattern.matcher(payload);
        return matcher.find() ? matcher.group(1) : null;
    }

}
