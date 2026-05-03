package com.pm.patientservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

import billing.BillingResponse;

class PatientServiceTest {

    private PatientRepository patientRepository;
    private BillingServiceGrpcClient billingServiceGrpcClient;
    private KafkaProducer kafkaProducer;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        billingServiceGrpcClient = mock(BillingServiceGrpcClient.class);
        kafkaProducer = mock(KafkaProducer.class);
        patientService = new PatientService(patientRepository, billingServiceGrpcClient, kafkaProducer);
    }

    @Test
    void getPatientsReturnsAllPatients() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("Alice");
        patient.setEmail("alice@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(java.time.LocalDate.parse("1990-01-01"));
        patient.setRegisteredDate(java.time.LocalDate.parse("2024-01-01"));

        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientResponseDTO> result = patientService.getPatients();

        assertEquals(1, result.size());
        assertEquals("alice@example.com", result.get(0).getEmail());
    }

    @Test
    void createPatientSavesPatientAndCallsExternalServices() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Bob");
        request.setEmail("bob@example.com");
        request.setAddress("456 Oak Ave");
        request.setDateOfBirth("1985-05-05");
        request.setRegisteredDate("2024-04-01");

        Patient savedPatient = new Patient();
        savedPatient.setId(UUID.randomUUID());
        savedPatient.setName(request.getName());
        savedPatient.setEmail(request.getEmail());
        savedPatient.setAddress(request.getAddress());
        savedPatient.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
        savedPatient.setRegisteredDate(java.time.LocalDate.parse(request.getRegisteredDate()));

        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        when(billingServiceGrpcClient.createBillingAccount(anyString(), anyString(), anyString()))
                .thenReturn(BillingResponse.newBuilder().setAccountId("123").setStatus("ACTIVE").build());
        doNothing().when(kafkaProducer).sendEvent(any(Patient.class));

        PatientResponseDTO created = patientService.createPatient(request);

        assertEquals(savedPatient.getEmail(), created.getEmail());
        assertEquals(savedPatient.getName(), created.getName());
        verify(patientRepository).save(any(Patient.class));
        verify(billingServiceGrpcClient, times(1)).createBillingAccount(anyString(), anyString(), anyString());
        verify(kafkaProducer, times(1)).sendEvent(any(Patient.class));
    }

    @Test
    void createPatientThrowsWhenEmailAlreadyExists() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setEmail("bob@example.com");

        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> patientService.createPatient(request));
    }

    @Test
    void updatePatientThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = new PatientRequestDTO();
        request.setEmail("new@example.com");

        when(patientRepository.existsByEmailAndIdNot(request.getEmail(), id)).thenReturn(false);
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> patientService.updatePatient(id, request));
    }

    @Test
    void deletePatientDeletesExistingPatient() {
        UUID id = UUID.randomUUID();
        when(patientRepository.existsById(id)).thenReturn(true);

        patientService.deletePatient(id);

        verify(patientRepository, times(1)).deleteById(id);
    }

    @Test
    void deletePatientThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(patientRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> patientService.deletePatient(id));
    }
}
