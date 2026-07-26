package com.sental.controller;

import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.EventCreateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventController {

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventController(EventService eventService, ObjectMapper objectMapper) {
        this.eventService = eventService;
        this.objectMapper = objectMapper;
    }

    public String create(EventCreateRequest request) {
        EventResponse response = eventService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        EventResponse response = eventService.findById(id);
        return toJson(response);
    }

    public String findAll() {
        List<EventResponse> list = eventService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        eventService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}