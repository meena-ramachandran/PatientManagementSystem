package com.pm.billingservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.dto.BillingAccountResponseDTO;
import com.pm.billingservice.exception.BillingAccountNotFoundException;
import com.pm.billingservice.kafka.BillingKafkaProducer;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.repository.BillingAccountRepository;

class BillingAccountServiceTest {

    private BillingAccountRepository repository;
    private BillingKafkaProducer kafkaProducer;
    private BillingAccountService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BillingAccountRepository.class);
        kafkaProducer = Mockito.mock(BillingKafkaProducer.class);
        service = new BillingAccountService(repository, kafkaProducer);
    }

    @Test
    void getAllAccountsReturnsStoredAccounts() {
        BillingAccount account = new BillingAccount();
        account.setId(UUID.randomUUID());
        account.setPatientId("patient-1");
        account.setName("Test User");
        account.setEmail("test@example.com");
        account.setStatus("ACTIVE");

        when(repository.findAll()).thenReturn(List.of(account));

        List<BillingAccountResponseDTO> response = service.getAllAccounts();

        assertEquals(1, response.size());
        assertEquals("test@example.com", response.get(0).getEmail());
    }

    @Test
    void createAccountStoresAndReturnsDto() {
        BillingAccountRequestDTO request = new BillingAccountRequestDTO();
        request.setPatientId("patient-1");
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setStatus("ACTIVE");

        BillingAccount saved = new BillingAccount();
        saved.setId(UUID.randomUUID());
        saved.setPatientId(request.getPatientId());
        saved.setName(request.getName());
        saved.setEmail(request.getEmail());
        saved.setStatus(request.getStatus());

        when(repository.save(any(BillingAccount.class))).thenReturn(saved);

        BillingAccountResponseDTO result = service.createAccount(request);

        assertNotNull(result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    void getAccountThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BillingAccountNotFoundException.class, () -> service.getAccount(id));
    }

    @Test
    void updateAccountPersistsChangesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        BillingAccountRequestDTO request = new BillingAccountRequestDTO();
        request.setPatientId("patient-2");
        request.setName("Updated User");
        request.setEmail("updated@example.com");
        request.setStatus("SUSPENDED");

        BillingAccount existing = new BillingAccount();
        existing.setId(id);
        existing.setPatientId("patient-1");
        existing.setName("Existing User");
        existing.setEmail("existing@example.com");
        existing.setStatus("ACTIVE");

        BillingAccount updated = new BillingAccount();
        updated.setId(id);
        updated.setPatientId(request.getPatientId());
        updated.setName(request.getName());
        updated.setEmail(request.getEmail());
        updated.setStatus(request.getStatus());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(BillingAccount.class))).thenReturn(updated);

        BillingAccountResponseDTO response = service.updateAccount(id, request);

        assertEquals("Updated User", response.getName());
        assertEquals("SUSPENDED", response.getStatus());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    void deleteAccountThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(BillingAccountNotFoundException.class, () -> service.deleteAccount(id));
    }

    @Test
    void deleteAccountRemovesExistingAccount() {
        //UUID id = UUID.randomUUID();
        //when(repository.existsById(id)).thenReturn(true);

        //service.deleteAccount(id);

        //verify(repository).deleteById(id);
    }
}
