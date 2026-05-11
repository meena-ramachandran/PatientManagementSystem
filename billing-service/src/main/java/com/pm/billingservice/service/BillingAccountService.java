package com.pm.billingservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.dto.BillingAccountResponseDTO;
import com.pm.billingservice.exception.BillingAccountNotFoundException;
import com.pm.billingservice.kafka.BillingKafkaProducer;
import com.pm.billingservice.mapper.BillingAccountMapper;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.repository.BillingAccountRepository;

@Service
public class BillingAccountService {

    private final BillingAccountRepository repository;
    private final BillingKafkaProducer kafkaProducer;

    public BillingAccountService(BillingAccountRepository repository, BillingKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<BillingAccountResponseDTO> getAllAccounts() {
        return repository.findAll().stream()
                .map(BillingAccountMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BillingAccountResponseDTO getAccount(UUID id) {
        BillingAccount account = repository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found with id: " + id));
        return BillingAccountMapper.toResponseDTO(account);
    }

    public BillingAccountResponseDTO createAccount(BillingAccountRequestDTO request) {
        return repository.findByPatientId(request.getPatientId())
                .map(BillingAccountMapper::toResponseDTO)
                .orElseGet(() -> {
                    BillingAccount account = BillingAccountMapper.toEntity(request);
                    BillingAccount saved = repository.save(account);
                    kafkaProducer.sendEvent("BILLING_ACCOUNT_CREATED", saved, null);
                    return BillingAccountMapper.toResponseDTO(saved);
                });
    }

    public BillingAccountResponseDTO updateAccount(UUID id, BillingAccountRequestDTO request) {
        BillingAccount existing = repository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found with id: " + id));
        existing.setPatientId(request.getPatientId());
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setStatus(request.getStatus());
        if (request.getBalance() != null) {
            existing.setBalance(request.getBalance());
        }
        BillingAccount updated = repository.save(existing);
        kafkaProducer.sendEvent("BILLING_ACCOUNT_UPDATED", updated, updated.getBalance().toPlainString());
        return BillingAccountMapper.toResponseDTO(updated);
    }

    public BillingAccountResponseDTO creditBalance(UUID id, java.math.BigDecimal amount) {
        BillingAccount existing = repository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found with id: " + id));
        existing.setBalance(existing.getBalance().add(amount));
        BillingAccount updated = repository.save(existing);
        kafkaProducer.sendEvent("BILLING_ACCOUNT_CREDITED", updated, amount.toPlainString());
        return BillingAccountMapper.toResponseDTO(updated);
    }

    public BillingAccountResponseDTO chargeBalance(UUID id, java.math.BigDecimal amount) {
        BillingAccount existing = repository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found with id: " + id));
        existing.setBalance(existing.getBalance().subtract(amount));
        BillingAccount updated = repository.save(existing);
        kafkaProducer.sendEvent("BILLING_ACCOUNT_CHARGED", updated, amount.toPlainString());
        return BillingAccountMapper.toResponseDTO(updated);
    }

    public void deleteAccount(UUID id) {
        BillingAccount existing = repository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found with id: " + id));
        kafkaProducer.sendEvent("BILLING_ACCOUNT_DELETED", existing, null);
        repository.deleteById(id);
    }

    public BillingAccountResponseDTO deleteAccountByPatientId(String patientId) {
        BillingAccount existing = repository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException("Billing account not found for patient id: " + patientId));
        kafkaProducer.sendEvent("BILLING_ACCOUNT_DELETED", existing, null);
        repository.delete(existing);
        return BillingAccountMapper.toResponseDTO(existing);
    }
}
