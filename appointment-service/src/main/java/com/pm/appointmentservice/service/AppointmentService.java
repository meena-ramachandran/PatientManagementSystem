package com.pm.appointmentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.grpc.AuthServiceGrpcClient;
import com.pm.appointmentservice.grpc.BillingServiceGrpcClient;
import com.pm.appointmentservice.kafka.KafkaProducer;
import com.pm.appointmentservice.mapper.AppointmentMapper;
import com.pm.appointmentservice.model.Appointment;
import com.pm.appointmentservice.repository.AppointmentRepository;

import billing.BillingResponse;

@Service
public class AppointmentService {

    private static final BigDecimal DEFAULT_APPOINTMENT_FEE = new BigDecimal("100.00");

    private final AppointmentRepository appointmentRepository;
    private final AuthServiceGrpcClient authServiceGrpcClient;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;

    public AppointmentService(AppointmentRepository appointmentRepository,
            AuthServiceGrpcClient authServiceGrpcClient,
            BillingServiceGrpcClient billingServiceGrpcClient,
            KafkaProducer kafkaProducer,
            RestTemplateBuilder restTemplateBuilder) {
        this.appointmentRepository = appointmentRepository;
        this.authServiceGrpcClient = authServiceGrpcClient;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<AppointmentResponseDTO> getAppointments() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentMapper::toResponseDTO)
                .toList();
    }

    public AppointmentResponseDTO getAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with id: " + id));
        return AppointmentMapper.toResponseDTO(appointment);
    }

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        Map<String, Object> patientInfo = validatePatient(request.getPatientId());
        if (!authServiceGrpcClient.userExists(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User not found with id: " + request.getUserId());
        }

        String patientName = (String) patientInfo.getOrDefault("name", "");
        String patientEmail = (String) patientInfo.getOrDefault("email", "");
        BillingResponse billingResponse = billingServiceGrpcClient.createBillingAccount(request.getPatientId(),
                patientName, patientEmail);

        BigDecimal appointmentFee = request.getAppointmentFee() != null ? request.getAppointmentFee()
                : DEFAULT_APPOINTMENT_FEE;
        billingServiceGrpcClient.chargeBillingAccount(billingResponse.getAccountId(), appointmentFee.toPlainString());

        Appointment appointment = AppointmentMapper.toAppointment(request);
        appointment.setBillingAccountId(billingResponse.getAccountId());
        appointment.setAppointmentFee(appointmentFee);
        Appointment saved = appointmentRepository.save(appointment);
        kafkaProducer.sendEvent(saved, "APPOINTMENT_CREATED");
        return AppointmentMapper.toResponseDTO(saved);
    }

    public AppointmentResponseDTO updateAppointment(UUID id, AppointmentRequestDTO request) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with id: " + id));

        if (!existing.getPatientId().toString().equals(request.getPatientId())) {
            validatePatient(request.getPatientId());
            existing.setPatientId(UUID.fromString(request.getPatientId()));
        }

        if (!authServiceGrpcClient.userExists(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User not found with id: " + request.getUserId());
        }

        BigDecimal newFee = request.getAppointmentFee() != null ? request.getAppointmentFee() : existing.getAppointmentFee();
        BigDecimal existingFee = existing.getAppointmentFee() != null ? existing.getAppointmentFee() : DEFAULT_APPOINTMENT_FEE;

        if (newFee.compareTo(existingFee) != 0) {
            BigDecimal diff = newFee.subtract(existingFee);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                billingServiceGrpcClient.chargeBillingAccount(existing.getBillingAccountId(), diff.toPlainString());
            } else {
                billingServiceGrpcClient.creditBillingAccount(existing.getBillingAccountId(), diff.abs().toPlainString());
            }
        }

        existing.setAppointmentFee(newFee);
        existing.setUserId(UUID.fromString(request.getUserId()));
        existing.setAppointmentDateTime(LocalDateTime.parse(request.getAppointmentDateTime()));
        existing.setStatus(request.getStatus());
        existing.setNotes(request.getNotes());

        Appointment updated = appointmentRepository.save(existing);
        kafkaProducer.sendEvent(updated, "APPOINTMENT_UPDATED");
        return AppointmentMapper.toResponseDTO(updated);
    }

    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with id: " + id));

        if (appointment.getBillingAccountId() != null && appointment.getAppointmentFee() != null) {
            billingServiceGrpcClient.creditBillingAccount(appointment.getBillingAccountId(),
                    appointment.getAppointmentFee().toPlainString());
        }

        appointmentRepository.deleteById(id);
    }

    private Map<String, Object> validatePatient(String patientId) {
        try {
            Map<String, Object> patient = restTemplate.getForObject("http://patient-service:4000/patients/{id}",
                    Map.class, patientId);
            if (patient == null || patient.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Patient not found with id: " + patientId);
            }
            return patient;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Patient not found with id: " + patientId);
        }
    }
}
