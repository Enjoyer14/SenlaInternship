package com.sental.repository;

import com.sental.config.TestPersistenceConfig;
import com.sental.entity.Venue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestPersistenceConfig.class)
class VenueRepositoryTest {
    @Autowired
    private VenueRepository venueRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void findByCity() {
        Venue venue = new Venue();
        venue.setName("VTB Arena");
        venue.setAddress("Main street, 1");
        venue.setCity("Moscow");
        venue.setRegion("Moscow region");
        venue.setCapacity(1000);
        venue.setCreatedAt(LocalDateTime.now());
        venueRepository.save(venue);
        entityManager.flush();

        List<Venue> venues = venueRepository.findByCity("Moscow");

        assertEquals(1, venues.size());
        assertEquals("VTB Arena", venues.get(0).getName());
    }
}