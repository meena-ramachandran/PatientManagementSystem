package com.pm.billingservice.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.dto.BillingAccountResponseDTO;
import com.pm.billingservice.service.BillingAccountService;

@RestController
@RequestMapping("/billing-accounts")
public class BillingAccountController {

    private final BillingAccountService billingAccountService;

    public BillingAccountController(BillingAccountService billingAccountService) {
        this.billingAccountService = billingAccountService;
    }

    @GetMapping
    public ResponseEntity<List<BillingAccountResponseDTO>> getAll() {
        return ResponseEntity.ok(billingAccountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingAccountResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingAccountService.getAccount(id));
    }

    @PostMapping
    public ResponseEntity<BillingAccountResponseDTO> create(@Valid @RequestBody BillingAccountRequestDTO request) {
        BillingAccountResponseDTO created = billingAccountService.createAccount(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingAccountResponseDTO> update(@PathVariable UUID id,
            @Valid @RequestBody BillingAccountRequestDTO request) {
        return ResponseEntity.ok(billingAccountService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        billingAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
