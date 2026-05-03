package com.pm.appointmentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.appointmentservice.model.Appointment;

import appointment.events.AppointmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        } catch (Exception e) {
            log.error("Failed to send Kafka event for appointment: {}", appointmentEvent, e);
        }
    }
}
