package com.sental.repository;

import com.sental.entity.Venue;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VenueRepository {

    private final Map<Long, Venue> store = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Venue> findAll() {
        return new ArrayList<>(store.values());
    }

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(id.getAndIncrement());
        }
        store.put(venue.getId(), venue);
        return venue;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}