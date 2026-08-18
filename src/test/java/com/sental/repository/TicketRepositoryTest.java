package com.sental.repository;

import com.sental.config.TestPersistenceConfig;
import com.sental.entity.Ticket;
import com.sental.enums.TicketStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestPersistenceConfig.class)
class TicketRepositoryTest {
    @Autowired
    private TicketRepository ticketRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void saveAndFindById() {
        Ticket ticket = createTicket("T-000001", 1L, 1L);
        Optional<Ticket> found = ticketRepository.findById(ticket.getId());

        assertTrue(found.isPresent());
        assertEquals("T-000001", found.get().getTicketNumber());
    }

    @Test
    @Transactional
    void findByUserId() {
        createTicket("T-000001", 1L, 1L);
        createTicket("T-000002", 1L, 2L);
        entityManager.flush();

        List<Ticket> tickets = ticketRepository.findByUserId(1L);

        assertEquals(2, tickets.size());
    }

    private Ticket createTicket(String number, Long userId, Long eventId) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(number);
        ticket.setUserId(userId);
        ticket.setEventId(eventId);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPaidAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}