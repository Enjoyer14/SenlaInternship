package com.sental.service;

import com.sental.dto.request.create.TicketCreateRequest;
import com.sental.dto.request.update.TicketUpdateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.entity.Ticket;
import com.sental.enums.TicketStatus;
import com.sental.exception.EntityNotFound;
import com.sental.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    @Transactional(readOnly = true)
    public TicketResponse findById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFound("Ticket not found: " + id));
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findAll() {
        List<TicketResponse> result = new ArrayList<>();
        for (Ticket ticket : ticketRepository.findAll()) {
            result.add(toResponse(ticket));
        }
        return result;
    }

    @Transactional
    public TicketResponse update(Long id, TicketUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFound("Ticket not found: " + id));

        ticket.setStatus(request.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFound("Ticket not found"));
        ticketRepository.delete(ticket);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .userId(ticket.getUserId())
                .eventId(ticket.getEventId())
                .status(ticket.getStatus())
                .paidAt(ticket.getPaidAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}