package com.sental.repository;

import com.sental.entity.Event;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EventRepository {

    private final Map<Long, Event> store = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Event> findAll() {
        return new ArrayList<>(store.values());
    }

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(id.getAndIncrement());
        }
        store.put(event.getId(), event);
        return event;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}