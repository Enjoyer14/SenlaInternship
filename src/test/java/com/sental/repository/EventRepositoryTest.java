package com.sental.repository;

import com.sental.config.TestPersistenceConfig;
import com.sental.entity.Category;
import com.sental.entity.Event;
import com.sental.entity.Venue;
import com.sental.enums.EventStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestPersistenceConfig.class)
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @Transactional
    void saveAndFindById() {
        Event event = createPersistedEvent();
        Optional<Event> found = eventRepository.findById(event.getId());

        assertTrue(found.isPresent());
        assertEquals("Java Conference", found.get().getTitle());
        assertEquals(1, found.get().getCategories().size());
        assertTrue(found.get().getOrganizerIds().contains(101L));
    }

    @Test
    @Transactional
    void findByTitleContaining() {
        createPersistedEvent();
        entityManager.flush();
        List<Event> events = eventRepository.findByTitleContaining("java");

        assertEquals(1, events.size());
        assertEquals("Java Conference", events.get(0).getTitle());
    }

    @Test
    @Transactional
    void findByStatus() {
        createPersistedEvent();
        entityManager.flush();

        List<Event> events = eventRepository.findByStatus(EventStatus.PUBLISHED);

        assertEquals(1, events.size());
    }

    @Test
    @Transactional
    void deleteById() {
        Event event = createPersistedEvent();
        Long id = event.getId();
        entityManager.flush();
        entityManager.clear();

        eventRepository.deleteById(id);
        entityManager.flush();

        assertTrue(eventRepository.findById(id).isEmpty());
    }

    @Test
    @Transactional
    void plainFindByIdLeavesAssociationsLazy() {
        Event event = createPersistedEvent();
        Long id = event.getId();
        entityManager.flush();
        entityManager.clear();

        Event loaded = eventRepository.findById(id).orElseThrow();
        PersistenceUnitUtil util = entityManagerFactory.getPersistenceUnitUtil();

        assertFalse(util.isLoaded(loaded.getCategories()));
        assertFalse(util.isLoaded(loaded.getOrganizerIds()));
    }

    @Test
    @Transactional
    void findByIdWithAssociationsCriteriaLoadsAssociations() {
        Event event = createPersistedEvent();
        Long id = event.getId();
        entityManager.flush();
        entityManager.clear();

        Event loaded = eventRepository.findByIdWithAssociationsCriteria(id).orElseThrow();
        assertAssociationsLoaded(loaded);
    }

    @Test
    @Transactional
    void findByIdWithAssociationsJpqlLoadsAssociations() {
        Event event = createPersistedEvent();
        Long id = event.getId();
        entityManager.flush();
        entityManager.clear();

        Event loaded = eventRepository.findByIdWithAssociationsJpql(id).orElseThrow();
        assertAssociationsLoaded(loaded);
    }

    @Test
    @Transactional
    void findByIdWithAssociationsEntityGraphLoadsAssociations() {
        Event event = createPersistedEvent();
        Long id = event.getId();
        entityManager.flush();
        entityManager.clear();

        Event loaded = eventRepository.findByIdWithAssociationsEntityGraph(id).orElseThrow();
        assertAssociationsLoaded(loaded);
    }

    private Event createPersistedEvent() {
        Venue venue = new Venue();
        venue.setName("VTB Arena");
        venue.setAddress("Main street, 1");
        venue.setCity("Moscow");
        venue.setRegion("Moscow region");
        venue.setCapacity(1000);
        venue.setCreatedAt(LocalDateTime.now());
        venueRepository.save(venue);

        Category category = new Category();
        category.setName("IT");
        categoryRepository.save(category);

        Event event = new Event();
        event.setVenue(venue);
        event.setTitle("Java Conference");
        event.setDescription("Spring and Hibernate");
        event.setEventDate(LocalDateTime.now().plusDays(10));
        event.setCapacity(700);
        event.setBookedSeats(0);
        event.setPrice(new BigDecimal("500.00"));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event.getCategories().add(category);
        event.getOrganizerIds().add(101L);

        return eventRepository.save(event);
    }

    private void assertAssociationsLoaded(Event event) {
        PersistenceUnitUtil util = entityManagerFactory.getPersistenceUnitUtil();

        assertTrue(util.isLoaded(event.getVenue()));
        assertTrue(util.isLoaded(event.getCategories()));
        assertTrue(util.isLoaded(event.getOrganizerIds()));
        assertEquals(1, event.getCategories().size());
        assertTrue(event.getOrganizerIds().contains(101L));
    }
}