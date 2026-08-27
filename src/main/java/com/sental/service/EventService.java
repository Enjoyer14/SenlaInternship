package com.sental.service;

import com.sental.dto.request.create.EventCreateRequest;
import com.sental.dto.request.update.EventUpdateRequest;
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

// TODO
//    @Transactional
//    public EventResponse addCategories(EventCreateRequest request, List<Long> categoryIds) {
//        Event saved = eventRepository.save(toEntity(request));
//        if (categoryIds != null) {
//            for (Long categoryId : categoryIds) {
//                Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFound("Category not found: " + categoryId));
//                saved.getCategories().add(category);
//            }
//        }
//        return toResponse(saved);
//    }

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
    public EventResponse update(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFound("Event not found: " + id));
        Venue venue = venueRepository.findById(request.getVenueId()).orElseThrow(() -> new EntityNotFound("Venue not found: " + request.getVenueId()));
        event.setVenue(venue);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice() == null ? null : BigDecimal.valueOf(request.getPrice()));
        event.setUpdatedAt(LocalDateTime.now());

        event.getCategories().clear();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                        new EntityNotFound("Category not found: " + categoryId));
                event.getCategories().add(category);
            }
        }

        Event updated = eventRepository.save(event);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFound("Event not found: " + id));
        eventRepository.delete(event);
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