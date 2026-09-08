package com.sental.controller;

import com.sental.dto.request.update.TicketUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.TicketCreateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@RequestBody TicketCreateRequest request) {
        return ticketService.create(request);
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable("id") Long id) {
        return ticketService.findById(id);
    }

    @GetMapping()
    public List<TicketResponse> findAll() {
        return ticketService.findAll();
    }

    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable("id") Long id, @RequestBody TicketUpdateRequest request) {
        return ticketService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        ticketService.delete(id);
    }


}