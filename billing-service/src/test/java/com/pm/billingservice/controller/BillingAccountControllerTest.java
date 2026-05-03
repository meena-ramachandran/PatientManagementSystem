package com.pm.billingservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.billingservice.dto.BillingAccountRequestDTO;
import com.pm.billingservice.dto.BillingAccountResponseDTO;
import com.pm.billingservice.service.BillingAccountService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BillingAccountController.class)
class BillingAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BillingAccountService billingAccountService;

    @Test
    void getAllReturnsAccountList() throws Exception {
        BillingAccountResponseDTO dto = new BillingAccountResponseDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setPatientId("patient-1");
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setStatus("ACTIVE");

        when(billingAccountService.getAllAccounts()).thenReturn(List.of(dto));

        mockMvc.perform(get("/billing-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void getByIdReturnsAccount() throws Exception {
        UUID id = UUID.randomUUID();
        BillingAccountResponseDTO dto = new BillingAccountResponseDTO();
        dto.setId(id.toString());
        dto.setPatientId("patient-1");
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setStatus("ACTIVE");

        when(billingAccountService.getAccount(id)).thenReturn(dto);

        mockMvc.perform(get("/billing-accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createReturnsCreatedAccount() throws Exception {
        BillingAccountRequestDTO request = new BillingAccountRequestDTO();
        request.setPatientId("patient-1");
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setStatus("ACTIVE");

        BillingAccountResponseDTO response = new BillingAccountResponseDTO();
        response.setId(UUID.randomUUID().toString());
        response.setPatientId(request.getPatientId());
        response.setName(request.getName());
        response.setEmail(request.getEmail());
        response.setStatus(request.getStatus());

        when(billingAccountService.createAccount(any(BillingAccountRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateReturnsUpdatedAccount() throws Exception {
        UUID id = UUID.randomUUID();
        BillingAccountRequestDTO request = new BillingAccountRequestDTO();
        request.setPatientId("patient-2");
        request.setName("Updated User");
        request.setEmail("updated@example.com");
        request.setStatus("SUSPENDED");

        BillingAccountResponseDTO response = new BillingAccountResponseDTO();
        response.setId(id.toString());
        response.setPatientId(request.getPatientId());
        response.setName(request.getName());
        response.setEmail(request.getEmail());
        response.setStatus(request.getStatus());

        when(billingAccountService.updateAccount(eq(id), any(BillingAccountRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/billing-accounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/billing-accounts/{id}", id))
                .andExpect(status().isNoContent());

        verify(billingAccountService).deleteAccount(id);
    }
}
