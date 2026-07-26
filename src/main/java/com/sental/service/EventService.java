package com.sental.service;

import com.sental.dto.request.EventCreateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.entity.Event;
import com.sental.enums.EventStatus;
import com.sental.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse create(EventCreateRequest request) {
        Event event = new Event();
        event.setVenueId(request.getVenueId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setBookedSeats(0);
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found: " + id));
        return toResponse(event);
    }

    public List<EventResponse> findAll() {
        List<EventResponse> result = new ArrayList<>();
        for (Event event : eventRepository.findAll()) {
            result.add(toResponse(event));
        }
        return result;
    }

    public void delete(Long id) {
        eventRepository.delete(id);
    }

    private EventResponse toResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setVenueId(event.getVenueId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setEventDate(event.getEventDate());
        response.setCapacity(event.getCapacity());
        response.setBookedSeats(event.getBookedSeats());
        response.setPrice(event.getPrice());
        response.setStatus(event.getStatus());
        response.setCreatedAt(event.getCreatedAt());
        return response;
    }
}