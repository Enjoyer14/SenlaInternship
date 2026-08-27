package com.sental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.TicketCreateRequest;
import com.sental.dto.request.update.TicketUpdateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.entity.Ticket;
import com.sental.enums.TicketStatus;
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

import java.time.LocalDateTime;

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
class TicketControllerIntegrationTest {

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

    @Test
    void create_shouldReturnCreatedTicket() throws Exception {
        TicketCreateRequest request = new TicketCreateRequest();
        request.setUserId(100L);
        request.setEventId(200L);

        MvcResult result = mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.ticketNumber", notNullValue()))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.eventId", is(200)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.paidAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", nullValue()))
                .andReturn();

        TicketResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), TicketResponse.class);
        Ticket saved = entityManager.find(Ticket.class, response.getId());
        assertNotNull(saved);
        assertEquals(100L, saved.getUserId());
        assertEquals(200L, saved.getEventId());
        assertEquals(TicketStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getPaidAt());
    }

    @Test
    void findById_shouldReturnTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber("TEST123");
        ticket.setUserId(5L);
        ticket.setEventId(6L);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPaidAt(LocalDateTime.now());
        entityManager.persist(ticket);
        entityManager.flush();

        mockMvc.perform(get("/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("$.ticketNumber", is("TEST123")))
                .andExpect(jsonPath("$.userId", is(5)))
                .andExpect(jsonPath("$.eventId", is(6)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/tickets/999"))    .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnList() throws Exception {
        Ticket ticket1 = new Ticket();
        ticket1.setTicketNumber("T1");
        ticket1.setUserId(1L);
        ticket1.setEventId(2L);
        ticket1.setStatus(TicketStatus.ACTIVE);
        ticket1.setPaidAt(LocalDateTime.now());

        Ticket ticket2 = new Ticket();
        ticket2.setTicketNumber("T2");
        ticket2.setUserId(3L);
        ticket2.setEventId(4L);
        ticket2.setStatus(TicketStatus.CANCELLED);
        ticket2.setPaidAt(LocalDateTime.now());

        entityManager.persist(ticket1);
        entityManager.persist(ticket2);
        entityManager.flush();

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].ticketNumber", hasItems("T1", "T2")));
    }

    @Test
    void update_shouldModifyStatus() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber("OLD");
        ticket.setUserId(7L);
        ticket.setEventId(8L);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPaidAt(LocalDateTime.now());
        entityManager.persist(ticket);
        entityManager.flush();

        TicketUpdateRequest updateRequest = new TicketUpdateRequest();
        updateRequest.setStatus(TicketStatus.CANCELLED);

        mockMvc.perform(put("/tickets/{id}", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("$.status", is("CANCELLED")))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        Ticket updated = entityManager.find(Ticket.class, ticket.getId());
        assertEquals(TicketStatus.CANCELLED, updated.getStatus());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void update_whenNotFound_shouldReturn404() throws Exception {
        TicketUpdateRequest updateRequest = new TicketUpdateRequest();
        updateRequest.setStatus(TicketStatus.CANCELLED);

        mockMvc.perform(put("/tickets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldRemoveTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber("DEL");
        ticket.setUserId(9L);
        ticket.setEventId(10L);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPaidAt(LocalDateTime.now());
        entityManager.persist(ticket);
        entityManager.flush();

        mockMvc.perform(delete("/tickets/{id}", ticket.getId())).andExpect(status().isNoContent());

        assertNull(entityManager.find(Ticket.class, ticket.getId()));
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/tickets/999")).andExpect(status().isNotFound());
    }
}