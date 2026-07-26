package com.sental.repository;

import com.sental.entity.Ticket;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class TicketRepository {

    private final Map<Long, Ticket> store = new HashMap<>();
    private Long id = 1L;

    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(store.values());
    }

    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(id++);
        }
        store.put(ticket.getId(), ticket);
        return ticket;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}