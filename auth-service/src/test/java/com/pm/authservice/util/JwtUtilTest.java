package com.pm.authservice.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        byte[] secretBytes = "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8);
        String secret = Base64.getEncoder().encodeToString(secretBytes);
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void generateTokenProducesValidJwt() {
        String token = jwtUtil.generateToken("test@example.com", "USER");

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateTokenThrowsForInvalidJwt() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtUtil.validateToken(invalidToken));
    }
}
