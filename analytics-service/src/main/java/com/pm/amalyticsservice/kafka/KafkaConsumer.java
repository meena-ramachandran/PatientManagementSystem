package com.pm.amalyticsservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import patient.events.PatientEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.service.AnalyticsEventService;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final AnalyticsEventService analyticsEventService;

    public KafkaConsumer(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event){
        try{
            PatientEvent patientEvent = PatientEvent.parseFrom(ByteString.copyFrom(event));

            AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
            request.setPatientId(patientEvent.getPatientId());
            request.setEventType(patientEvent.getEventType());
            request.setDetails("name=" + patientEvent.getName() + ", email=" + patientEvent.getEmail());

            var saved = analyticsEventService.createEvent(request);
            log.info("Persisted analytics event with id={} from Kafka message", saved.getId());
        }catch(InvalidProtocolBufferException e){
            log.error("Error deserializing patient event: {}", e.getMessage());
        }
    }

}
