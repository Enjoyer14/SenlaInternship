package com.sental.controller;

import com.sental.dto.request.update.EventUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.EventCreateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody EventCreateRequest request) {
        return eventService.create(request);
    }

// TODO
//    @PostMapping("/{eventId}/categories")
//    public EventResponse addCategories(
//            @PathVariable Long eventId,
//            @RequestBody List<Long> categoryIds) {
//        return eventService.addCategories(eventId, categoryIds);
//    }

    @GetMapping("/{id}")
    public EventResponse findById(@PathVariable("id") Long id) {
        return eventService.findById(id);
    }

    @GetMapping()
    public List<EventResponse> findAll() {
        return eventService.findAll();
    }

    @PutMapping("/{id}")
    public EventResponse update(@PathVariable("id") Long id, @RequestBody EventUpdateRequest request) {
        return eventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        eventService.delete(id);
    }

}