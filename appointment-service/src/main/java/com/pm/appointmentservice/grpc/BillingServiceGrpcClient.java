package com.pm.appointmentservice.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingAmountRequest;
import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    public BillingServiceGrpcClient(@Value("${billing.service.address:billing-service}") String serverAddress,
            @Value("${billing.service.port:9095}") int serverPort) {
        log.info("Connecting to BillingService at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();

        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received response from BillingService via gRPC: {}", response);
        return response;
    }

    public BillingResponse chargeBillingAccount(String accountId, String amount) {
        BillingAmountRequest request = BillingAmountRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount)
                .build();

        BillingResponse response = blockingStub.chargeBillingAccount(request);
        log.info("Charged billing account {} with amount {} via gRPC: {}", accountId, amount, response);
        return response;
    }

    public BillingResponse creditBillingAccount(String accountId, String amount) {
        BillingAmountRequest request = BillingAmountRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount)
                .build();

        BillingResponse response = blockingStub.creditBillingAccount(request);
        log.info("Credited billing account {} with amount {} via gRPC: {}", accountId, amount, response);
        return response;
    }
}
