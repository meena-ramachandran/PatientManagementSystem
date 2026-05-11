package com.pm.billingservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.billingservice.model.BillingAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BillingKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(BillingKafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public BillingKafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String eventType, BillingAccount account, String amount) {
        String details = String.format("accountId=%s,patientId=%s,name=%s,email=%s,amount=%s,balance=%s", 
                account.getId(), account.getPatientId(), account.getName(), account.getEmail(),
                amount == null ? "" : amount, account.getBalance());

        String payload = String.format("{\"eventType\":\"%s\",\"patientId\":\"%s\",\"details\":\"%s\"}",
                eventType,
                account.getPatientId(),
                details.replace("\"", "\\\""));

        try {
            kafkaTemplate.send("billing", payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            log.info("Sent Kafka billing event: {}", payload);
        } catch (Exception e) {
            log.error("Failed to send Kafka billing event: {}", payload, e);
        }
    }
}
