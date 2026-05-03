package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.service.BillingAccountService;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    private final BillingAccountService billingAccountService;

    public BillingGrpcService(BillingAccountService billingAccountService) {
        this.billingAccountService = billingAccountService;
    }

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("createBillingAccount request received: {}", billingRequest.toString());

        BillingAccountRequestDTO request = new BillingAccountRequestDTO();
        request.setPatientId(billingRequest.getPatientId());
        request.setName(billingRequest.getName());
        request.setEmail(billingRequest.getEmail());
        request.setStatus("ACTIVE");

        var account = billingAccountService.createAccount(request);

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(account.getId())
                .setStatus(account.getStatus())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void creditBillingAccount(billing.BillingAmountRequest request,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("creditBillingAccount request received: {}", request.toString());
        if (request.getAccountId().isBlank()) {
            responseObserver.onError(new IllegalArgumentException("Missing accountId for credit operation"));
            return;
        }

        var account = billingAccountService.creditBalance(UUID.fromString(request.getAccountId()),
                new java.math.BigDecimal(request.getAmount()));

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(account.getId())
                .setStatus(account.getStatus())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void chargeBillingAccount(billing.BillingAmountRequest request,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("chargeBillingAccount request received: {}", request.toString());
        if (request.getAccountId().isBlank()) {
            responseObserver.onError(new IllegalArgumentException("Missing accountId for charge operation"));
            return;
        }

        var account = billingAccountService.chargeBalance(UUID.fromString(request.getAccountId()),
                new java.math.BigDecimal(request.getAmount()));

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(account.getId())
                .setStatus(account.getStatus())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBillingAccountByPatientId(billing.BillingPatientRequest request,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("deleteBillingAccountByPatientId request received: {}", request.toString());
        var account = billingAccountService.deleteAccountByPatientId(request.getPatientId());

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(account.getId())
                .setStatus("DELETED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
