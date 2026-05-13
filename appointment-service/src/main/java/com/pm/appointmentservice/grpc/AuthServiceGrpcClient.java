package com.pm.appointmentservice.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.GetUserRequest;
import user.GetUserResponse;
import user.UserServiceGrpc;
//import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthServiceGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceGrpcClient.class);

   ;

    public AuthServiceGrpcClient(@Value("${auth.service.address:auth-service}") String serverAddress,
            @Value("${auth.service.port:9097}") int serverPort,
            GrpcClientInterceptor grpcClientInterceptor) {
        log.info("Connecting to AuthService at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().intercept(grpcClientInterceptor).build();
        blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public boolean userExists(String userId) {
        GetUserRequest request = GetUserRequest.newBuilder().setUserId(userId).build();
        GetUserResponse response = blockingStub.getUser(request);
        log.info("Received user existence response from AuthService via gRPC: {}", response.getExists());
        return response.getExists();
    }
}
