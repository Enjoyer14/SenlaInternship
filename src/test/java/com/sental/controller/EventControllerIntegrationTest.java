package com.sental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.EventCreateRequest;
import com.sental.dto.request.update.EventUpdateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.entity.Category;
import com.sental.entity.Event;
import com.sental.entity.Venue;
import com.sental.enums.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        TestPersistenceConfig.class,
        WebConfig.class
})
@Transactional
class EventControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private Venue createVenue(String name, String address, String city, int capacity) {
        Venue venue = new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setCity(city);
        venue.setRegion("Test Region");
        venue.setCapacity(capacity);
        venue.setCreatedAt(LocalDateTime.now());
        entityManager.persist(venue);
        return venue;
    }

    @Test
    void create_shouldReturnCreatedEvent() throws Exception {
        Venue venue = createVenue("Test Venue", "123 Main St", "Test City", 100);

        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        entityManager.flush();

        EventCreateRequest request = EventCreateRequest.builder()
                .venueId(venue.getId())
                .title("Integration Test Event")
                .description("Created via MockMvc")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(150)
                .price(19.99)
                .categoryIds(List.of(category.getId()))
                .build();

        MvcResult result = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Event")))
                .andExpect(jsonPath("$.venueId", is(venue.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PUBLISHED")))
                .andExpect(jsonPath("$.price", is(19.99)))
                .andReturn();

        EventResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), EventResponse.class);
        Event saved = entityManager.find(Event.class, response.getId());
        assertNotNull(saved);
        assertEquals("Integration Test Event", saved.getTitle());
    }

    @Test
    void create_withInvalidVenue_shouldReturn404() throws Exception {
        EventCreateRequest request = EventCreateRequest.builder()
                .venueId(999L)
                .title("Invalid Venue")
                .description("Should fail")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(10)
                .price(5.0)
                .build();

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnEvent() throws Exception {
        Venue venue = createVenue("Venue for Find", "456 Oak Ave", "Test City", 100);

        Event event = Event.builder()
                .venue(venue)
                .title("Find Me")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(25.00))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event);
        entityManager.flush();

        mockMvc.perform(get("/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Find Me")))
                .andExpect(jsonPath("$.venueId", is(venue.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PUBLISHED")));
    }

    @Test
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/events/999")).andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnList() throws Exception {
        Venue venue = createVenue("Venue List", "789 Pine St", "Test City", 100);

        Event event1 = Event.builder()
                .venue(venue)
                .title("Event A")
                .description("A")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(50)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(10.0))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        Event event2 = Event.builder()
                .venue(venue)
                .title("Event B")
                .description("B")
                .eventDate(LocalDateTime.now().plusDays(2))
                .capacity(80)
                .bookedSeats(5)
                .price(BigDecimal.valueOf(20.0))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].title", hasItems("Event A", "Event B")));
    }

    @Test
    void update_shouldModifyEvent() throws Exception {
        Venue oldVenue = createVenue("Old Venue", "111 Old Str", "Test City", 100);
        Venue newVenue = createVenue("New Venue", "222 New Blvd", "Test City", 150);

        Category category1 = new Category();
        category1.setName("Cat1");
        entityManager.persist(category1);
        Category category2 = new Category();
        category2.setName("Cat2");
        entityManager.persist(category2);
        entityManager.flush();

        Event event = Event.builder()
                .venue(oldVenue)
                .title("Old Title")
                .description("Old Desc")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(100)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(9.99))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event);
        entityManager.flush();

        EventUpdateRequest updateRequest = new EventUpdateRequest();
        updateRequest.setVenueId(newVenue.getId());
        updateRequest.setTitle("New Title");
        updateRequest.setDescription("New Desc");
        updateRequest.setEventDate(LocalDateTime.now().plusDays(5));
        updateRequest.setCapacity(200);
        updateRequest.setPrice(49.99);
        updateRequest.setCategoryIds(List.of(category1.getId(), category2.getId()));

        mockMvc.perform(put("/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(jsonPath("$.title", is("New Title")))
                .andExpect(jsonPath("$.venueId", is(newVenue.getId().intValue())))
                .andExpect(jsonPath("$.price", is(49.99)));

        Event updated = entityManager.find(Event.class, event.getId());
        assertEquals("New Title", updated.getTitle());
        assertEquals(newVenue.getId(), updated.getVenue().getId());
        assertEquals(2, updated.getCategories().size());
    }

    @Test
    void update_withInvalidVenue_shouldReturn404() throws Exception {
        Venue venue = createVenue("Venue", "333 Invalid Rd", "Test City", 100);

        Event event = Event.builder()
                .venue(venue)
                .title("Title")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(50)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(10.0))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event);
        entityManager.flush();

        EventUpdateRequest updateRequest = new EventUpdateRequest();
        updateRequest.setVenueId(999L);
        updateRequest.setTitle("Updated");
        updateRequest.setDescription("Updated");
        updateRequest.setEventDate(LocalDateTime.now().plusDays(2));
        updateRequest.setCapacity(60);
        updateRequest.setPrice(12.0);

        mockMvc.perform(put("/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_withInvalidCategory_shouldReturn404() throws Exception {
        Venue venue = createVenue("Venue", "444 Invalid Cat St", "Test City", 100);

        Event event = Event.builder()
                .venue(venue)
                .title("Title")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(50)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(10.0))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event);
        entityManager.flush();

        EventUpdateRequest updateRequest = new EventUpdateRequest();
        updateRequest.setVenueId(venue.getId());
        updateRequest.setTitle("Updated");
        updateRequest.setDescription("Updated");
        updateRequest.setEventDate(LocalDateTime.now().plusDays(2));
        updateRequest.setCapacity(60);
        updateRequest.setPrice(12.0);
        updateRequest.setCategoryIds(List.of(999L));

        mockMvc.perform(put("/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldRemoveEvent() throws Exception {
        Venue venue = createVenue("Venue for Delete", "555 Delete Ln", "Test City", 100);

        Event event = Event.builder()
                .venue(venue)
                .title("To Delete")
                .description("Delete me")
                .eventDate(LocalDateTime.now().plusDays(1))
                .capacity(30)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(5.0))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(event);
        entityManager.flush();

        mockMvc.perform(delete("/events/{id}", event.getId()))
                .andExpect(status().isNoContent());

        assertNull(entityManager.find(Event.class, event.getId()));
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/events/999")).andExpect(status().isNotFound());
    }
}