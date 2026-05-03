package com.pm.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;

import io.jsonwebtoken.JwtException;

class AuthServiceTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userService, passwordEncoder, jwtUtil);
    }

    @Test
    void authenticateReturnsTokenWhenCredentialsMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");
        user.setRole("USER");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-pass", "encoded-pass")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "USER")).thenReturn("token-123");

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("plain-pass");

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isPresent());
        assertEquals("token-123", result.get());
    }

    @Test
    void authenticateReturnsEmptyWhenUserDoesNotExist() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("missing@example.com");
        request.setPassword("plain-pass");

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateReturnsEmptyWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "encoded-pass")).thenReturn(false);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("wrong-pass");

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);

        assertTrue(authService.validateToken("valid-token"));
        verify(jwtUtil).validateToken("valid-token");
    }

    @Test
    void validateTokenReturnsFalseForInvalidToken() {
        doThrow(new JwtException("invalid")).when(jwtUtil).validateToken("bad-token");

        assertFalse(authService.validateToken("bad-token"));
        verify(jwtUtil).validateToken("bad-token");
    }
}
