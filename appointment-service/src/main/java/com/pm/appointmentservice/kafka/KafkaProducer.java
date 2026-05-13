package com.pm.appointmentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.appointmentservice.model.Appointment;

import appointment.events.AppointmentEvent;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.MDC;


@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Appointment appointment, String eventType) {
        AppointmentEvent appointmentEvent = AppointmentEvent.newBuilder()
                .setAppointmentId(appointment.getId().toString())
                .setPatientId(appointment.getPatientId().toString())
                .setUserId(appointment.getUserId().toString())
                .setBillingAccountId(appointment.getBillingAccountId() == null ? "" : appointment.getBillingAccountId())
                .setAppointmentDateTime(appointment.getAppointmentDateTime().toString())
                .setStatus(appointment.getStatus())
                .setNotes(appointment.getNotes() == null ? "" : appointment.getNotes())
                .setEventType(eventType)
                .build();

        try {
            kafkaTemplate.send("appointment", appointmentEvent.toByteArray());
            log.info("Sent Kafka event for appointment: {}", appointmentEvent);

             String correlationId =
            MDC.get("correlationId");

    ProducerRecord<String, byte[]> record =
            new ProducerRecord<>(
                    "patient-events",
                    appointmentEvent.toByteArray()
            );

    if (correlationId != null) {

        record.headers().add(
                "X-Correlation-Id",
                correlationId.getBytes(StandardCharsets.UTF_8)
        );
    }

    kafkaTemplate.send(record);

    log.info(
            "Appointment event sent for appointmentId={} eventType={}",
            appointment.getId(),
            eventType
    );

        } catch (Exception e) {
            log.error("Failed to send Kafka event for appointment: {}", appointmentEvent, e);
        }
    }
}
