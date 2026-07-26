package com.sental.repository;

import com.sental.entity.Venue;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class VenueRepository {

    private final Map<Long, Venue> store = new HashMap<>();
    private Long id = 1L;

    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Venue> findAll() {
        return new ArrayList<>(store.values());
    }

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(id++);
        }
        store.put(venue.getId(), venue);
        return venue;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}