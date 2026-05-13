package com.pm.billingservice.grpc;

import io.grpc.*;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GrpcGlobalServerInterceptor
public class GrpcServerInterceptor
        implements ServerInterceptor {

    public static final Metadata.Key<String>
            CORRELATION_ID_KEY =
            Metadata.Key.of(
                    "X-Correlation-Id",
                    Metadata.ASCII_STRING_MARSHALLER
            );

    @Override
    public <ReqT, RespT>
    ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {

        String correlationId =
                headers.get(CORRELATION_ID_KEY);

        if (correlationId != null) {
            MDC.put("correlationId", correlationId);
        }

        return next.startCall(call, headers);
    }
}
