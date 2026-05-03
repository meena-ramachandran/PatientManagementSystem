package com.pm.billingservice.mapper;

import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.dto.BillingAccountResponseDTO;
import com.pm.billingservice.model.BillingAccount;

public class BillingAccountMapper {

    public static BillingAccountResponseDTO toResponseDTO(BillingAccount billingAccount) {
        BillingAccountResponseDTO dto = new BillingAccountResponseDTO();
        dto.setId(billingAccount.getId().toString());
        dto.setPatientId(billingAccount.getPatientId());
        dto.setName(billingAccount.getName());
        dto.setEmail(billingAccount.getEmail());
        dto.setStatus(billingAccount.getStatus());
        dto.setBalance(billingAccount.getBalance());
        return dto;
    }

    public static BillingAccount toEntity(BillingAccountRequestDTO request) {
        BillingAccount account = new BillingAccount();
        account.setPatientId(request.getPatientId());
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setStatus(request.getStatus());
        if (request.getBalance() != null) {
            account.setBalance(request.getBalance());
        }
        return account;
    }
}
