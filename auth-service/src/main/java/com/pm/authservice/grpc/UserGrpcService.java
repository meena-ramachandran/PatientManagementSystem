package com.pm.authservice.grpc;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pm.authservice.model.User;
import com.pm.authservice.service.UserService;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import user.GetUserRequest;
import user.GetUserResponse;
import user.UserServiceGrpc;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(UserGrpcService.class);
    private final UserService userService;

    public UserGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        log.info("Received gRPC user lookup for id={}", request.getUserId());

        GetUserResponse.Builder responseBuilder = GetUserResponse.newBuilder();
        try {
            Optional<User> user = userService.findById(UUID.fromString(request.getUserId()));
            if (user.isPresent()) {
                responseBuilder.setUserId(user.get().getId().toString())
                        .setEmail(user.get().getEmail())
                        .setRole(user.get().getRole())
                        .setExists(true);
            } else {
                responseBuilder.setExists(false);
            }
        } catch (IllegalArgumentException e) {
            responseBuilder.setExists(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
