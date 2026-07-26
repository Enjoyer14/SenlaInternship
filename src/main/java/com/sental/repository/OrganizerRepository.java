package com.sental.repository;

import com.sental.entity.Organizer;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class OrganizerRepository {

    private final Map<Long, Organizer> store = new HashMap<>();
    private Long id = 1L;

    public Optional<Organizer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Organizer> findAll() {
        return new ArrayList<>(store.values());
    }

    public Organizer save(Organizer organizer) {
        if (organizer.getId() == null) {
            organizer.setId(id++);
        }
        store.put(organizer.getId(), organizer);
        return organizer;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}