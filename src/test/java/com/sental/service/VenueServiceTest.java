package com.sental.service;

import com.sental.dto.request.create.VenueCreateRequest;
import com.sental.dto.request.update.VenueUpdateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.entity.Venue;
import com.sental.exception.EntityNotFound;
import com.sental.repository.VenueRepository;
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
class VenueServiceTest {
    @Mock
    private VenueRepository venueRepository;
    @InjectMocks
    private VenueService venueService;

    private Venue testVenue;
    private VenueCreateRequest createRequest;
    private VenueUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testVenue = new Venue();
        testVenue.setId(1L);
        testVenue.setName("Main Hall");
        testVenue.setAddress("123 Main St");
        testVenue.setCity("New York");
        testVenue.setRegion("NY");
        testVenue.setCapacity(500);
        testVenue.setCreatedAt(LocalDateTime.now());
        testVenue.setUpdatedAt(null);

        createRequest = new VenueCreateRequest();
        createRequest.setName("Garden Arena");
        createRequest.setAddress("456 Garden Ave");
        createRequest.setCity("Los Angeles");
        createRequest.setRegion("CA");
        createRequest.setCapacity(200);

        updateRequest = new VenueUpdateRequest();
        updateRequest.setName("Updated Arena");
        updateRequest.setAddress("789 New St");
        updateRequest.setCity("San Francisco");
        updateRequest.setRegion("CA");
        updateRequest.setCapacity(300);
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        when(venueRepository.save(any(Venue.class))).thenAnswer(invocation -> {
            Venue saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });
        VenueResponse response = venueService.create(createRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Garden Arena", response.getName());
        assertEquals("456 Garden Ave", response.getAddress());
        assertEquals("Los Angeles", response.getCity());
        assertEquals("CA", response.getRegion());
        assertEquals(200, response.getCapacity());
        assertNotNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());

        verify(venueRepository, times(1)).save(any(Venue.class));
    }

    @Test
    void findById_whenExists_shouldReturnResponse() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        VenueResponse response = venueService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Main Hall", response.getName());
        assertEquals("New York", response.getCity());

        verify(venueRepository, times(1)).findById(1L);
    }

    @Test
    void findById_whenNotFound_shouldThrowEntityNotFound() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> venueService.findById(999L));

        verify(venueRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_shouldReturnListOfResponses() {
        Venue another = new Venue();
        another.setId(2L);
        another.setName("Small Hall");
        another.setCity("Chicago");
        another.setRegion("IL");
        another.setCapacity(100);
        another.setCreatedAt(LocalDateTime.now());

        when(venueRepository.findAll()).thenReturn(List.of(testVenue, another));

        List<VenueResponse> responses = venueService.findAll();
        assertEquals(2, responses.size());
        assertEquals("Main Hall", responses.get(0).getName());
        assertEquals("Small Hall", responses.get(1).getName());

        verify(venueRepository, times(1)).findAll();
    }

    @Test
    void update_shouldModifyAndReturnResponse() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(venueRepository.save(any(Venue.class))).thenAnswer(invocation -> invocation.getArgument(0));
        VenueResponse response = venueService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Arena", response.getName());
        assertEquals("789 New St", response.getAddress());
        assertEquals("San Francisco", response.getCity());
        assertEquals("CA", response.getRegion());
        assertEquals(300, response.getCapacity());
        assertNotNull(response.getUpdatedAt());

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).save(testVenue);
    }

    @Test
    void update_whenNotFound_shouldThrowEntityNotFound() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> venueService.update(999L, updateRequest));

        verify(venueRepository, times(1)).findById(999L);
        verify(venueRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldCallRepositoryDelete() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        doNothing().when(venueRepository).delete(testVenue);
        venueService.delete(1L);

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).delete(testVenue);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFound() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> venueService.delete(999L));

        verify(venueRepository, times(1)).findById(999L);
        verify(venueRepository, never()).delete(any());
    }
}