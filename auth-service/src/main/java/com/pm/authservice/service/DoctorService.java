package com.pm.authservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.pm.authservice.dto.DoctorRequestDTO;
import com.pm.authservice.model.Doctor;
import com.pm.authservice.model.User;
import com.pm.authservice.repository.DoctorRepository;
import com.pm.authservice.repository.UserRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Doctor createDoctor(DoctorRequestDTO request) {
        if (doctorRepository.findByEmail(request.getEmail()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // 1. Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("PHYSICIAN");
        User savedUser = userRepository.save(user);

        // 2. Create Doctor Profile
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setUserId(savedUser.getId());

        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public Doctor updateDoctor(UUID id, DoctorRequestDTO request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + id));

        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());

        // Check if email changed
        if (!doctor.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (doctorRepository.findByEmail(request.getEmail()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists: " + request.getEmail());
            }

            doctor.setEmail(request.getEmail());
            User user = userRepository.findById(doctor.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated user not found"));
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }

        // Check if password update is requested
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            User user = userRepository.findById(doctor.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated user not found"));
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }

        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + id));

        doctorRepository.delete(doctor);
        userRepository.deleteById(doctor.getUserId());
    }
}
