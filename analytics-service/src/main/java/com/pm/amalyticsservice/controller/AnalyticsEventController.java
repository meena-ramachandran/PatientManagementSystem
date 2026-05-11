package com.pm.amalyticsservice.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.dto.AnalyticsEventResponseDTO;
import com.pm.amalyticsservice.dto.AnalyticsSummaryDTO;
import com.pm.amalyticsservice.service.AnalyticsEventService;

@RestController
@RequestMapping("/analytics-events")
public class AnalyticsEventController {

    private final AnalyticsEventService analyticsEventService;

    public AnalyticsEventController(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsEventResponseDTO>> getAll(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String patientId) {
        return ResponseEntity.ok(analyticsEventService.getEvents(eventType, patientId));
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String patientId) {
        return ResponseEntity.ok(analyticsEventService.getSummary(eventType, patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsEventResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(analyticsEventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<AnalyticsEventResponseDTO> create(@Valid @RequestBody AnalyticsEventRequestDTO request) {
        return ResponseEntity.ok(analyticsEventService.createEvent(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsEventResponseDTO> update(@PathVariable UUID id,
            @Valid @RequestBody AnalyticsEventRequestDTO request) {
        return ResponseEntity.ok(analyticsEventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        analyticsEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
