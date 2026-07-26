package com.sental.service;

import com.sental.dto.request.TicketCreateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.entity.Ticket;
import com.sental.enums.TicketStatus;
import com.sental.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketResponse create(TicketCreateRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(UUID.randomUUID().toString().substring(0, 8));
        ticket.setUserId(request.getUserId());
        ticket.setEventId(request.getEventId());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPaidAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        return toResponse(saved);
    }

    public TicketResponse findById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
        return toResponse(ticket);
    }

    public List<TicketResponse> findAll() {
        List<TicketResponse> result = new ArrayList<>();
        for (Ticket ticket : ticketRepository.findAll()) {
            result.add(toResponse(ticket));
        }
        return result;
    }

    public void delete(Long id) {
        ticketRepository.delete(id);
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTicketNumber(ticket.getTicketNumber());
        response.setUserId(ticket.getUserId());
        response.setEventId(ticket.getEventId());
        response.setStatus(ticket.getStatus());
        response.setPaidAt(ticket.getPaidAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }
}