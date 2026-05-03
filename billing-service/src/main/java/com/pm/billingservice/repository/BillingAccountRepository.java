package com.pm.billingservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.billingservice.model.BillingAccount;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, UUID> {
}
