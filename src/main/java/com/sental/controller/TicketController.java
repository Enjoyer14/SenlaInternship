package com.sental.controller;

import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.TicketCreateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketController {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TicketController(TicketService ticketService, ObjectMapper objectMapper) {
        this.ticketService = ticketService;
        this.objectMapper = objectMapper;
    }

    public String create(TicketCreateRequest request) {
        TicketResponse response = ticketService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        TicketResponse response = ticketService.findById(id);
        return toJson(response);
    }

    public String findAll() {
        List<TicketResponse> list = ticketService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        ticketService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}