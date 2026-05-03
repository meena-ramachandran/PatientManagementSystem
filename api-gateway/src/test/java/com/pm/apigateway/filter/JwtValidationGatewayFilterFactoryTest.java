package com.pm.apigateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.client.WebClient;

class JwtValidationGatewayFilterFactoryTest {

    private JwtValidationGatewayFilterFactory filterFactory;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        when(webClientBuilder.baseUrl("http://localhost:8080")).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        filterFactory = new JwtValidationGatewayFilterFactory(webClientBuilder, "http://localhost:8080");
    }

    @Test
    void filterReturnsUnauthorizedWhenAuthorizationHeaderIsMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.GET, "/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = filterFactory.apply(new Object());
        filter.filter(exchange, (serverWebExchange) -> {
            throw new IllegalStateException("Filter chain should not be called");
        }).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void filterReturnsUnauthorizedWhenAuthorizationHeaderIsMalformed() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.GET, "/test")
                .header("Authorization", "BadHeader token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = filterFactory.apply(new Object());
        filter.filter(exchange, (serverWebExchange) -> {
            throw new IllegalStateException("Filter chain should not be called");
        }).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
