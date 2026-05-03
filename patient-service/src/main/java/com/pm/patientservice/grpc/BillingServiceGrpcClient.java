package com.pm.patientservice.grpc;

import org.hibernate.engine.spi.Managed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import billing.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String serverAddress, @Value("${billing.service.port:9095}") int serverPort) {
        log.info("Connecting to BillingService at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public billing.BillingResponse createBillingAccount(String patientId, String name, String email) {
        billing.BillingRequest request = billing.BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();
        
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received response from BillingService via gRPC: {}", response.toString());
        return response;
    }
}
