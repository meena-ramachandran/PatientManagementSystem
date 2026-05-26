package com.pm.authservice.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.dto.UserRequestDTO;
import com.pm.authservice.dto.UserResponseDTO;
import com.pm.authservice.dto.DoctorRequestDTO;
import com.pm.authservice.dto.DoctorResponseDTO;
import com.pm.authservice.model.Doctor;
import com.pm.authservice.model.User;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.UserService;
import com.pm.authservice.service.DoctorService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final DoctorService doctorService;

    public AuthController(AuthService authService, UserService userService, DoctorService doctorService) {
        this.authService = authService;
        this.userService = userService;
        this.doctorService = doctorService;
    }

    @Operation(summary = "Login endpoint", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        
        Optional<String> tokenOpt = authService.authenticate(loginRequest);
        if(tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOpt.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));
        
    }

    @Operation(summary = "Register User", description = "Public registration endpoint for new users (patients, staff)")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        User savedUser = userService.createUser(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId().toString());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Register Doctor", description = "Public registration endpoint for new doctors (creates user + doctor profile)")
    @PostMapping("/register-doctor")
    public ResponseEntity<DoctorResponseDTO> registerDoctor(@Valid @RequestBody DoctorRequestDTO request) {
        Doctor doctor = doctorService.createDoctor(request);
        DoctorResponseDTO response = new DoctorResponseDTO();
        response.setId(doctor.getId().toString());
        response.setName(doctor.getName());
        response.setSpecialization(doctor.getSpecialization());
        response.setEmail(doctor.getEmail());
        response.setPhone(doctor.getPhone());
        response.setUserId(doctor.getUserId().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Validate Token", description = "Validates a JWT token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Authorization: Bearer <token>

        if(authHeader==null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<io.jsonwebtoken.Claims> claimsOpt = authService.getClaims(authHeader.substring(7));
        if (claimsOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        io.jsonwebtoken.Claims claims = claimsOpt.get();
        String role = claims.get("role", String.class);
        String email = claims.getSubject();

        return ResponseEntity.ok()
                .header("X-User-Role", role != null ? role : "")
                .header("X-User-Email", email != null ? email : "")
                .build();
    }

} 