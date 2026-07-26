package com.sental.controller;

import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.VenueCreateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VenueController {

    private final VenueService venueService;
    private final ObjectMapper objectMapper;

    @Autowired
    public VenueController(VenueService venueService, ObjectMapper objectMapper) {
        this.venueService = venueService;
        this.objectMapper = objectMapper;
    }

    public String create(VenueCreateRequest request) {
        VenueResponse response = venueService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        VenueResponse response = venueService.findById(id);
        return toJson(response);
    }

    public String findAll() {
        List<VenueResponse> list = venueService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        venueService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}