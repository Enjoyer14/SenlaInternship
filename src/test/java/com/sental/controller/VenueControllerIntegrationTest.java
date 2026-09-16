package com.sental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.SecurityConfig;
import com.sental.config.TestApplicationConfig;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.VenueCreateRequest;
import com.sental.dto.request.update.VenueUpdateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        TestPersistenceConfig.class,
        TestApplicationConfig.class,
        WebConfig.class,
        SecurityConfig.class
})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-test-secret-key-test-secret-key-123456",
        "jwt.expiration=3600000"
})
@Transactional
class VenueControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreatedVenue() throws Exception {
        VenueCreateRequest request = new VenueCreateRequest();
        request.setName("Test Venue");
        request.setAddress("123 Test St");
        request.setCity("Test City");
        request.setRegion("TS");
        request.setCapacity(150);

        MvcResult result = mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Test Venue")))
                .andExpect(jsonPath("$.address", is("123 Test St")))
                .andExpect(jsonPath("$.city", is("Test City")))
                .andExpect(jsonPath("$.region", is("TS")))
                .andExpect(jsonPath("$.capacity", is(150)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", nullValue()))
                .andReturn();

        VenueResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), VenueResponse.class);
        Venue saved = entityManager.find(Venue.class, response.getId());
        assertNotNull(saved);
        assertEquals("Test Venue", saved.getName());
        assertEquals("123 Test St", saved.getAddress());
        assertEquals("Test City", saved.getCity());
        assertEquals("TS", saved.getRegion());
        assertEquals(150, saved.getCapacity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnVenue() throws Exception {
        Venue venue = new Venue();
        venue.setName("Find Me");
        venue.setAddress("Find Address");
        venue.setCity("Find City");
        venue.setRegion("FR");
        venue.setCapacity(100);
        venue.setCreatedAt(LocalDateTime.now());
        entityManager.persist(venue);
        entityManager.flush();

        mockMvc.perform(get("/venues/{id}", venue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(venue.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Find Me")))
                .andExpect(jsonPath("$.city", is("Find City")))
                .andExpect(jsonPath("$.capacity", is(100)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/venues/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAll_shouldReturnList() throws Exception {
        Venue venue1 = new Venue();
        venue1.setName("Venue A");
        venue1.setAddress("A");
        venue1.setCity("City A");
        venue1.setRegion("RA");
        venue1.setCapacity(10);
        venue1.setCreatedAt(LocalDateTime.now());

        Venue venue2 = new Venue();
        venue2.setName("Venue B");
        venue2.setAddress("B");
        venue2.setCity("City B");
        venue2.setRegion("RB");
        venue2.setCapacity(20);
        venue2.setCreatedAt(LocalDateTime.now());

        entityManager.persist(venue1);
        entityManager.persist(venue2);
        entityManager.flush();

        mockMvc.perform(get("/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Venue A", "Venue B")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldModifyVenue() throws Exception {
        Venue venue = new Venue();
        venue.setName("Old Name");
        venue.setAddress("Old Address");
        venue.setCity("Old City");
        venue.setRegion("OR");
        venue.setCapacity(50);
        venue.setCreatedAt(LocalDateTime.now());
        entityManager.persist(venue);
        entityManager.flush();

        VenueUpdateRequest updateRequest = new VenueUpdateRequest();
        updateRequest.setName("New Name");
        updateRequest.setAddress("New Address");
        updateRequest.setCity("New City");
        updateRequest.setRegion("NR");
        updateRequest.setCapacity(200);

        mockMvc.perform(put("/venues/{id}", venue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(venue.getId().intValue())))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.address", is("New Address")))
                .andExpect(jsonPath("$.city", is("New City")))
                .andExpect(jsonPath("$.region", is("NR")))
                .andExpect(jsonPath("$.capacity", is(200)))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        Venue updated = entityManager.find(Venue.class, venue.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("New Address", updated.getAddress());
        assertEquals("New City", updated.getCity());
        assertEquals("NR", updated.getRegion());
        assertEquals(200, updated.getCapacity());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenNotFound_shouldReturn404() throws Exception {
        VenueUpdateRequest updateRequest = new VenueUpdateRequest();
        updateRequest.setName("Any");
        updateRequest.setAddress("Any");
        updateRequest.setCity("Any");
        updateRequest.setRegion("Any");
        updateRequest.setCapacity(1);

        mockMvc.perform(put("/venues/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRemoveVenue() throws Exception {
        Venue venue = new Venue();
        venue.setName("To Delete");
        venue.setAddress("Del Address");
        venue.setCity("Del City");
        venue.setRegion("DR");
        venue.setCapacity(10);
        venue.setCreatedAt(LocalDateTime.now());
        entityManager.persist(venue);
        entityManager.flush();

        mockMvc.perform(delete("/venues/{id}", venue.getId())).andExpect(status().isNoContent());

        assertNull(entityManager.find(Venue.class, venue.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/venues/999")).andExpect(status().isNotFound());
    }
}