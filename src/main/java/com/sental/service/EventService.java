package com.sental.service;

import com.sental.dto.request.EventCreateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.entity.Category;
import com.sental.entity.Event;
import com.sental.entity.Venue;
import com.sental.enums.EventStatus;
import com.sental.exception.EntityNotFound;
import com.sental.repository.CategoryRepository;
import com.sental.repository.EventRepository;
import com.sental.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public EventService(EventRepository eventRepository, VenueRepository venueRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public EventResponse create(EventCreateRequest request) {
        Event saved = eventRepository.save(toEntity(request));
        return toResponse(saved);
    }

    @Transactional
    public EventResponse createWithCategories(EventCreateRequest request, List<Long> categoryIds) {
        Event saved = eventRepository.save(toEntity(request));
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFound("Category not found: " + categoryId));
                saved.getCategories().add(category);
            }
        }
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFound("Event not found: " + id));
        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        List<EventResponse> result = new ArrayList<>();
        for (Event event : eventRepository.findAll()) {
            result.add(toResponse(event));
        }
        return result;
    }

    @Transactional
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    private Event toEntity(EventCreateRequest request) {
        Venue venue = venueRepository.findById(request.getVenueId()).orElseThrow(() -> new EntityNotFound("Venue not found: " + request.getVenueId()));
        return Event.builder()
                .venue(venue)
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .capacity(request.getCapacity())
                .bookedSeats(0)
                .price(request.getPrice() == null ? null : BigDecimal.valueOf(request.getPrice()))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .venueId(event.getVenue() == null ? null : event.getVenue().getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .bookedSeats(event.getBookedSeats())
                .price(event.getPrice() == null ? null : event.getPrice().doubleValue())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
    }
}