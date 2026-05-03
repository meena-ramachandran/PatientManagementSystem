package com.pm.amalyticsservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.exception.AnalyticsEventNotFoundException;
import com.pm.amalyticsservice.model.AnalyticsEvent;
import com.pm.amalyticsservice.repository.AnalyticsEventRepository;

class AnalyticsEventServiceTest {

    private AnalyticsEventRepository repository;
    private AnalyticsEventService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(AnalyticsEventRepository.class);
        service = new AnalyticsEventService(repository);
    }

    @Test
    void createEventReturnsSavedResponse() {
        AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
        request.setPatientId("patient-123");
        request.setEventType("CHECKIN");
        request.setDetails("Patient checked in for appointment.");

        AnalyticsEvent saved = new AnalyticsEvent();
        saved.setId(UUID.randomUUID());
        saved.setPatientId(request.getPatientId());
        saved.setEventType(request.getEventType());
        saved.setDetails(request.getDetails());

        when(repository.save(any(AnalyticsEvent.class))).thenReturn(saved);

        var response = service.createEvent(request);

        assertNotNull(response.getId());
        assertEquals("CHECKIN", response.getEventType());
    }

    @Test
    void getEventByIdThrowsNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AnalyticsEventNotFoundException.class, () -> service.getEventById(id));
    }

    @Test
    void updateEventReturnsUpdatedEvent() {
        UUID id = UUID.randomUUID();
        AnalyticsEventRequestDTO request = new AnalyticsEventRequestDTO();
        request.setPatientId("patient-789");
        request.setEventType("CHECKOUT");
        request.setDetails("Patient completed checkout.");

        AnalyticsEvent existing = new AnalyticsEvent();
        existing.setId(id);
        existing.setPatientId("patient-123");
        existing.setEventType("CHECKIN");
        existing.setDetails("Initial event.");

        AnalyticsEvent updated = new AnalyticsEvent();
        updated.setId(id);
        updated.setPatientId(request.getPatientId());
        updated.setEventType(request.getEventType());
        updated.setDetails(request.getDetails());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(AnalyticsEvent.class))).thenReturn(updated);

        var response = service.updateEvent(id, request);

        assertEquals("CHECKOUT", response.getEventType());
        assertEquals("patient-789", response.getPatientId());
        assertEquals("Patient completed checkout.", response.getDetails());
    }

    @Test
    void deleteEventRemovesExistingEvent() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteEvent(id);

        verify(repository).deleteById(id);
    }
}
