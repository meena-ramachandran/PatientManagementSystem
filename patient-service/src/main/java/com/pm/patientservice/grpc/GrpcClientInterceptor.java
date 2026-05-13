package com.pm.patientservice.grpc;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalClientInterceptor
public class GrpcClientInterceptor
        implements ClientInterceptor {

    public static final Metadata.Key<String>
            CORRELATION_ID_KEY =
            Metadata.Key.of(
                    "X-Correlation-Id",
                    Metadata.ASCII_STRING_MARSHALLER
            );

    @Override
    public <ReqT, RespT>
    ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {

        return new ForwardingClientCall
                .SimpleForwardingClientCall<>(
                        next.newCall(method, callOptions)
                ) {

            @Override
            public void start(
                    Listener<RespT> responseListener,
                    Metadata headers
            ) {

                String correlationId =
                        MDC.get("correlationId");

                if (correlationId != null) {

                    headers.put(
                            CORRELATION_ID_KEY,
                            correlationId
                    );
                }

                super.start(responseListener, headers);
            }
        };
    }
}