package com.sental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.OrganizerCreateRequest;
import com.sental.dto.request.update.OrganizerUpdateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.entity.Organizer;
import com.sental.entity.User;
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
class OrganizerControllerIntegrationTest {

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

    private User createTestUser() {
        User user = new User();
        user.setUsername("test_organizer_user");
        user.setEmail("org@test.com");
        user.setPasswordHash("pass");
        user.setRole(com.sental.enums.UserRole.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @Test
    void create_shouldReturnCreatedOrganizer() throws Exception {
        User user = createTestUser();

        OrganizerCreateRequest request = new OrganizerCreateRequest();
        request.setUserId(user.getId());
        request.setCompanyName("Test Company");
        request.setDescription("Test Desc");
        request.setPhone("+123456");

        MvcResult result = mockMvc.perform(post("/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.companyName", is("Test Company")))
                .andExpect(jsonPath("$.description", is("Test Desc")))
                .andExpect(jsonPath("$.phone", is("+123456")))
                .andReturn();

        OrganizerResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrganizerResponse.class);
        Organizer saved = entityManager.find(Organizer.class, response.getId());
        assertNotNull(saved);
        assertEquals("Test Company", saved.getCompanyName());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void create_withInvalidUser_shouldReturn404() throws Exception {
        OrganizerCreateRequest request = new OrganizerCreateRequest();
        request.setUserId(999L);
        request.setCompanyName("Invalid");
        request.setDescription("Desc");
        request.setPhone("+");

        mockMvc.perform(post("/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnOrganizer() throws Exception {
        User user = createTestUser();
        Organizer organizer = new Organizer();
        organizer.setUser(user);
        organizer.setCompanyName("Find Me");
        organizer.setDescription("Desc");
        organizer.setPhone("+999");
        entityManager.persist(organizer);
        entityManager.flush();

        mockMvc.perform(get("/organizers/{id}", organizer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(organizer.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.companyName", is("Find Me")))
                .andExpect(jsonPath("$.phone", is("+999")));
    }

    @Test
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/organizers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnList() throws Exception {
        User user1 = createTestUser();

        User user2 = new User();
        user2.setUsername("test_organizer_user_2");
        user2.setEmail("org2@test.com");
        user2.setPasswordHash("pass");
        user2.setRole(com.sental.enums.UserRole.USER);
        user2.setCreatedAt(java.time.LocalDateTime.now());
        entityManager.persist(user2);

        Organizer org1 = new Organizer();
        org1.setUser(user1);
        org1.setCompanyName("Org A");
        org1.setDescription("A");
        org1.setPhone("1");

        Organizer org2 = new Organizer();
        org2.setUser(user2);
        org2.setCompanyName("Org B");
        org2.setDescription("B");
        org2.setPhone("2");

        entityManager.persist(org1);
        entityManager.persist(org2);
        entityManager.flush();

        mockMvc.perform(get("/organizers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].companyName", hasItems("Org A", "Org B")));
    }

    @Test
    void update_shouldModifyOrganizer() throws Exception {
        User user = createTestUser();
        Organizer organizer = new Organizer();
        organizer.setUser(user);
        organizer.setCompanyName("Old Name");
        organizer.setDescription("Old Desc");
        organizer.setPhone("OldPhone");
        entityManager.persist(organizer);
        entityManager.flush();

        OrganizerUpdateRequest updateRequest = new OrganizerUpdateRequest();
        updateRequest.setCompanyName("New Name");
        updateRequest.setDescription("New Desc");
        updateRequest.setPhone("NewPhone");

        mockMvc.perform(put("/organizers/{id}", organizer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(organizer.getId().intValue())))
                .andExpect(jsonPath("$.companyName", is("New Name")))
                .andExpect(jsonPath("$.description", is("New Desc")))
                .andExpect(jsonPath("$.phone", is("NewPhone")));

        Organizer updated = entityManager.find(Organizer.class, organizer.getId());
        assertEquals("New Name", updated.getCompanyName());
        assertEquals("New Desc", updated.getDescription());
        assertEquals("NewPhone", updated.getPhone());
    }

    @Test
    void update_whenNotFound_shouldReturn404() throws Exception {
        OrganizerUpdateRequest updateRequest = new OrganizerUpdateRequest();
        updateRequest.setCompanyName("Any");
        updateRequest.setDescription("Any");
        updateRequest.setPhone("Any");

        mockMvc.perform(put("/organizers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldRemoveOrganizer() throws Exception {
        User user = createTestUser();
        Organizer organizer = new Organizer();
        organizer.setUser(user);
        organizer.setCompanyName("To Delete");
        organizer.setDescription("Desc");
        organizer.setPhone("+");
        entityManager.persist(organizer);
        entityManager.flush();

        mockMvc.perform(delete("/organizers/{id}", organizer.getId())).andExpect(status().isNoContent());

        assertNull(entityManager.find(Organizer.class, organizer.getId()));
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/organizers/999")).andExpect(status().isNotFound());
    }
}