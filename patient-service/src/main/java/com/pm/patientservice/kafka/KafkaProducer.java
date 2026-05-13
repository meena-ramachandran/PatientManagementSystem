package com.pm.patientservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.patientservice.model.Patient;

import patient.events.PatientEvent;

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

    public void sendEvent(Patient patient) {
        sendEvent(patient, "PATIENT_CREATED");
    }

    public void sendEvent(Patient patient, String eventType) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType(eventType)
                .build();

        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
            log.info("Sent Kafka event for patient: {}", patientEvent.toString());

            String correlationId = MDC.get("correlationId");

            ProducerRecord<String, byte[]> record =new ProducerRecord<>(
                            "patient-events",
                            patientEvent.toByteArray()
                    );

            if (correlationId != null) {
                record.headers().add(
                        "X-Correlation-Id",
                        correlationId.getBytes(StandardCharsets.UTF_8)
                );
            }
            kafkaTemplate.send(record);
            log.info(
                    "Patient event sent for patientId={} eventType={}",
                    patient.getId(),
                    eventType
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to send Kafka event for patient: {}", patientEvent.toString());
        }
    }

}
