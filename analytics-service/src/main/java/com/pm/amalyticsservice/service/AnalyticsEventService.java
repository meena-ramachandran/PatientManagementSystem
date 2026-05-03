package com.pm.amalyticsservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pm.amalyticsservice.dto.AnalyticsEventRequestDTO;
import com.pm.amalyticsservice.dto.AnalyticsEventResponseDTO;
import com.pm.amalyticsservice.exception.AnalyticsEventNotFoundException;
import com.pm.amalyticsservice.mapper.AnalyticsEventMapper;
import com.pm.amalyticsservice.model.AnalyticsEvent;
import com.pm.amalyticsservice.repository.AnalyticsEventRepository;

@Service
public class AnalyticsEventService {

    private final AnalyticsEventRepository repository;

    public AnalyticsEventService(AnalyticsEventRepository repository) {
        this.repository = repository;
    }

    public List<AnalyticsEventResponseDTO> getAllEvents() {
        return repository.findAll().stream()
                .map(AnalyticsEventMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AnalyticsEventResponseDTO getEventById(UUID id) {
        AnalyticsEvent event = repository.findById(id)
                .orElseThrow(() -> new AnalyticsEventNotFoundException("Analytics event not found with id: " + id));
        return AnalyticsEventMapper.toResponseDTO(event);
    }

    public AnalyticsEventResponseDTO createEvent(AnalyticsEventRequestDTO request) {
        AnalyticsEvent event = AnalyticsEventMapper.toEntity(request);
        AnalyticsEvent saved = repository.save(event);
        return AnalyticsEventMapper.toResponseDTO(saved);
    }

    public AnalyticsEventResponseDTO updateEvent(UUID id, AnalyticsEventRequestDTO request) {
        AnalyticsEvent existing = repository.findById(id)
                .orElseThrow(() -> new AnalyticsEventNotFoundException("Analytics event not found with id: " + id));
        existing.setPatientId(request.getPatientId());
        existing.setEventType(request.getEventType());
        existing.setDetails(request.getDetails());
        AnalyticsEvent updated = repository.save(existing);
        return AnalyticsEventMapper.toResponseDTO(updated);
    }

    public void deleteEvent(UUID id) {
        if (!repository.existsById(id)) {
            throw new AnalyticsEventNotFoundException("Analytics event not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
