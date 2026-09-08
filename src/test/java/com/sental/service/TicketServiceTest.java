package com.sental.service;

import com.sental.dto.request.create.TicketCreateRequest;
import com.sental.dto.request.update.TicketUpdateRequest;
import com.sental.dto.response.TicketResponse;
import com.sental.entity.Ticket;
import com.sental.enums.TicketStatus;
import com.sental.exception.EntityNotFound;
import com.sental.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;
    private TicketCreateRequest createRequest;
    private TicketUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTicketNumber("ABC123");
        testTicket.setUserId(10L);
        testTicket.setEventId(20L);
        testTicket.setStatus(TicketStatus.ACTIVE);
        testTicket.setPaidAt(LocalDateTime.now());
        testTicket.setUpdatedAt(null);

        createRequest = new TicketCreateRequest();
        createRequest.setUserId(10L);
        createRequest.setEventId(20L);

        updateRequest = new TicketUpdateRequest();
        updateRequest.setStatus(TicketStatus.CANCELLED);
    }

    @Test
    void create_shouldGenerateTicketNumberAndSetStatusActive() {
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        TicketResponse response = ticketService.create(createRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertNotNull(response.getTicketNumber());
        assertEquals(8, response.getTicketNumber().length()); // UUID substring
        assertEquals(10L, response.getUserId());
        assertEquals(20L, response.getEventId());
        assertEquals(TicketStatus.ACTIVE, response.getStatus());
        assertNotNull(response.getPaidAt());

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void findById_whenExists_shouldReturnResponse() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        TicketResponse response = ticketService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ABC123", response.getTicketNumber());
        assertEquals(10L, response.getUserId());
        assertEquals(20L, response.getEventId());
        assertEquals(TicketStatus.ACTIVE, response.getStatus());

        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void findById_whenNotFound_shouldThrowEntityNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> ticketService.findById(999L));

        verify(ticketRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_shouldReturnListOfResponses() {
        Ticket another = new Ticket();
        another.setId(2L);
        another.setTicketNumber("XYZ789");
        another.setUserId(11L);
        another.setEventId(21L);
        another.setStatus(TicketStatus.CANCELLED);
        another.setPaidAt(LocalDateTime.now());

        when(ticketRepository.findAll()).thenReturn(List.of(testTicket, another));

        List<TicketResponse> responses = ticketService.findAll();

        assertEquals(2, responses.size());
        assertEquals("ABC123", responses.get(0).getTicketNumber());
        assertEquals("XYZ789", responses.get(1).getTicketNumber());

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void update_shouldModifyStatusAndUpdatedAt() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response = ticketService.update(1L, updateRequest);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(TicketStatus.CANCELLED, response.getStatus());
        assertNotNull(response.getUpdatedAt());

        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(testTicket);
    }

    @Test
    void update_whenNotFound_shouldThrowEntityNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> ticketService.update(999L, updateRequest));

        verify(ticketRepository, times(1)).findById(999L);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldCallRepositoryDelete() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        doNothing().when(ticketRepository).delete(testTicket);
        ticketService.delete(1L);

        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).delete(testTicket);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> ticketService.delete(999L));

        verify(ticketRepository, times(1)).findById(999L);
        verify(ticketRepository, never()).delete(any());
    }
}