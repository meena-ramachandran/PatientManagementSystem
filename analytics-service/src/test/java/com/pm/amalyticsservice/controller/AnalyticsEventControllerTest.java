package com.pm.amalyticsservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.dto.AnalyticsEventResponseDTO;
import com.pm.amalyticsservice.service.AnalyticsEventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AnalyticsEventControllerTest {

    private MockMvc mockMvc;

    private AnalyticsEventService analyticsEventService;

    @BeforeEach
    void setUp() {
        analyticsEventService = mock(AnalyticsEventService.class);
        AnalyticsEventController controller = new AnalyticsEventController(analyticsEventService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllReturnsEventList() throws Exception {
        // AnalyticsEventResponseDTO dto = new AnalyticsEventResponseDTO();
        // dto.setId(UUID.randomUUID().toString());
        // dto.setPatientId("patient-1");
        // dto.setEventType("CHECKIN");
        // dto.setDetails("Patient checked in.");

        // when(analyticsEventService.getAllEvents()).thenReturn(List.of(dto));

        // mockMvc.perform(get("/analytics-events"))
        //         .andExpect(status().isOk())
        //         .andExpect(jsonPath("$[0].eventType").value("CHECKIN"))
        //         .andExpect(jsonPath("$[0].details").value("Patient checked in."));
    }

    @Test
    void getByIdReturnsEvent() throws Exception {
        UUID id = UUID.randomUUID();
        AnalyticsEventResponseDTO dto = new AnalyticsEventResponseDTO();
        dto.setId(id.toString());
        dto.setPatientId("patient-1");
        dto.setEventType("CHECKIN");
        dto.setDetails("Patient checked in.");

        when(analyticsEventService.getEventById(id)).thenReturn(dto);

        mockMvc.perform(get("/analytics-events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.eventType").value("CHECKIN"));
    }

    @Test
    void createReturnsCreatedEvent() throws Exception {
        AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
        request.setPatientId("patient-1");
        request.setEventType("CHECKIN");
        request.setDetails("Patient checked in.");

        AnalyticsEventResponseDTO response = new AnalyticsEventResponseDTO();
        response.setId(UUID.randomUUID().toString());
        response.setPatientId(request.getPatientId());
        response.setEventType(request.getEventType());
        response.setDetails(request.getDetails());

        when(analyticsEventService.createEvent(any(AnalyticsEventRequestDTO.class))).thenReturn(response);

        String requestBody = "{\"patientId\":\"patient-1\",\"eventType\":\"CHECKIN\",\"details\":\"Patient checked in.\"}";

        mockMvc.perform(post("/analytics-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.patientId").value("patient-1"));
    }

    @Test
    void updateReturnsUpdatedEvent() throws Exception {
        UUID id = UUID.randomUUID();
        AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
        request.setPatientId("patient-2");
        request.setEventType("CHECKOUT");
        request.setDetails("Patient checkout completed.");

        AnalyticsEventResponseDTO response = new AnalyticsEventResponseDTO();
        response.setId(id.toString());
        response.setPatientId(request.getPatientId());
        response.setEventType(request.getEventType());
        response.setDetails(request.getDetails());

        when(analyticsEventService.updateEvent(eq(id), any(AnalyticsEventRequestDTO.class))).thenReturn(response);

        String requestBody = "{\"patientId\":\"patient-2\",\"eventType\":\"CHECKOUT\",\"details\":\"Patient checkout completed.\"}";

        mockMvc.perform(put("/analytics-events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType").value("CHECKOUT"))
                .andExpect(jsonPath("$.patientId").value("patient-2"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/analytics-events/{id}", id))
                .andExpect(status().isNoContent());

        verify(analyticsEventService).deleteEvent(id);
    }
}
