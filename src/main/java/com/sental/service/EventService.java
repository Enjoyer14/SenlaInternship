package com.sental.service;

import com.sental.annotation.Transaction;
import com.sental.dto.request.EventCreateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.entity.Event;
import com.sental.enums.EventStatus;
import com.sental.exception.EntityNotFound;
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
        Event saved = eventRepository.save(toEntity(request));
        return toResponse(saved);
    }

    @Transaction
    public EventResponse createWithCategories(EventCreateRequest request, List<Long> categoryIds) {
        Event saved = eventRepository.save(toEntity(request));
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                eventRepository.addCategory(saved.getId(), categoryId);
            }
        }
        return toResponse(saved);
    }

    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFound("Event not found: " + id));
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

    private Event toEntity(EventCreateRequest request) {
        return Event.builder()
                .venueId(request.getVenueId())
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .bookedSeats(0)
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .venueId(event.getVenueId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .bookedSeats(event.getBookedSeats())
                .price(event.getPrice())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
    }
}