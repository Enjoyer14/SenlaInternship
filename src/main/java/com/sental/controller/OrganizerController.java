package com.sental.controller;

import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.OrganizerCreateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganizerController {

    private final OrganizerService organizerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrganizerController(OrganizerService organizerService, ObjectMapper objectMapper) {
        this.organizerService = organizerService;
        this.objectMapper = objectMapper;
    }

    public String create(OrganizerCreateRequest request) {
        OrganizerResponse response = organizerService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        OrganizerResponse response = organizerService.findById(id);
        return toJson(response);
    }

    public String findAll() {
        List<OrganizerResponse> list = organizerService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        organizerService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}