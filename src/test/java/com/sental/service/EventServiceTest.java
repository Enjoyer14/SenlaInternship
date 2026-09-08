package com.sental.service;

import com.sental.dto.request.create.EventCreateRequest;
import com.sental.dto.request.update.EventUpdateRequest;
import com.sental.dto.response.EventResponse;
import com.sental.entity.Category;
import com.sental.entity.Event;
import com.sental.entity.Venue;
import com.sental.enums.EventStatus;
import com.sental.exception.EntityNotFound;
import com.sental.repository.CategoryRepository;
import com.sental.repository.EventRepository;
import com.sental.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private EventService eventService;

    private Venue testVenue;
    private Category testCategory;
    private Event testEvent;
    private EventCreateRequest createRequest;
    private EventUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testVenue = new Venue();
        testVenue.setId(1L);
        testVenue.setName("Main Hall");
        testCategory = new Category();
        testCategory.setId(10L);
        testCategory.setName("Concert");
        testEvent = Event.builder()
                .id(100L)
                .venue(testVenue)
                .title("Rock Concert")
                .description("Amazing show")
                .eventDate(LocalDateTime.now().plusDays(5))
                .capacity(500)
                .bookedSeats(0)
                .price(BigDecimal.valueOf(49.99))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        createRequest = EventCreateRequest.builder()
                .venueId(1L)
                .title("Jazz Night")
                .description("Smooth jazz")
                .eventDate(LocalDateTime.now().plusDays(3))
                .capacity(200)
                .price(29.99)
                .categoryIds(List.of(10L))
                .build();

        updateRequest = new EventUpdateRequest();
        updateRequest.setVenueId(2L);
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Desc");
        updateRequest.setEventDate(LocalDateTime.now().plusDays(10));
        updateRequest.setCapacity(300);
        updateRequest.setPrice(39.99);
        updateRequest.setCategoryIds(List.of(20L, 30L));
    }

    @Test
    void create_shouldSaveAndReturnEventResponse() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        EventResponse response = eventService.create(createRequest);
        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("Jazz Night", response.getTitle());
        assertEquals(1L, response.getVenueId());
        assertEquals(EventStatus.PUBLISHED, response.getStatus());

        verify(venueRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void create_withInvalidVenue_shouldThrowEntityNotFound() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setVenueId(999L);
        assertThrows(EntityNotFound.class, () -> eventService.create(createRequest));

        verify(venueRepository, times(1)).findById(999L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void findById_whenExists_shouldReturnResponse() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        EventResponse response = eventService.findById(100L);
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Rock Concert", response.getTitle());
        assertEquals(1L, response.getVenueId());
        assertEquals(49.99, response.getPrice());

        verify(eventRepository, times(1)).findById(100L);
    }

    @Test
    void findById_whenNotFound_shouldThrowEntityNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> eventService.findById(999L));

        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_shouldReturnListOfResponses() {
        Event anotherEvent = Event.builder()
                .id(101L)
                .venue(testVenue)
                .title("Another Event")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .bookedSeats(10)
                .price(BigDecimal.valueOf(15.00))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(testEvent, anotherEvent));
        List<EventResponse> responses = eventService.findAll();

        assertEquals(2, responses.size());
        assertEquals("Rock Concert", responses.get(0).getTitle());
        assertEquals("Another Event", responses.get(1).getTitle());

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void update_shouldModifyEventAndReturnResponse() {
        Venue newVenue = new Venue();
        newVenue.setId(2L);
        newVenue.setName("New Venue");
        Category cat1 = new Category();
        cat1.setId(20L);
        cat1.setName("Category 1");
        Category cat2 = new Category();
        cat2.setId(30L);
        cat2.setName("Category 2");

        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        when(venueRepository.findById(2L)).thenReturn(Optional.of(newVenue));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(cat1));
        when(categoryRepository.findById(30L)).thenReturn(Optional.of(cat2));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse response = eventService.update(100L, updateRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Updated Title", response.getTitle());
        assertEquals(2L, response.getVenueId());
        assertEquals(39.99, response.getPrice());
        assertEquals(2, testEvent.getCategories().size());
        assertTrue(testEvent.getCategories().stream().anyMatch(c -> c.getId().equals(20L)));
        assertTrue(testEvent.getCategories().stream().anyMatch(c -> c.getId().equals(30L)));

        verify(eventRepository, times(1)).findById(100L);
        verify(venueRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).findById(20L);
        verify(categoryRepository, times(1)).findById(30L);
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void update_withNullCategoryIds_shouldClearCategories() {
        updateRequest.setCategoryIds(null);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        when(venueRepository.findById(2L)).thenReturn(Optional.of(new Venue()));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Category oldCat = new Category();
        oldCat.setId(5L);
        testEvent.getCategories().add(oldCat);
        EventResponse response = eventService.update(100L, updateRequest);
        assertTrue(testEvent.getCategories().isEmpty());

        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void update_whenEventNotFound_shouldThrowEntityNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> eventService.update(999L, updateRequest));

        verify(eventRepository, times(1)).findById(999L);
        verify(venueRepository, never()).findById(any());
        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_whenVenueNotFound_shouldThrowEntityNotFound() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());
        updateRequest.setVenueId(999L);
        assertThrows(EntityNotFound.class, () -> eventService.update(100L, updateRequest));

        verify(eventRepository, times(1)).findById(100L);
        verify(venueRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_whenCategoryNotFound_shouldThrowEntityNotFound() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        when(venueRepository.findById(2L)).thenReturn(Optional.of(new Venue()));
        when(categoryRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> eventService.update(100L, updateRequest));

        verify(eventRepository, times(1)).findById(100L);
        verify(venueRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).findById(20L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldCallDelete() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).delete(testEvent);
        eventService.delete(100L);

        verify(eventRepository, times(1)).findById(100L);
        verify(eventRepository, times(1)).delete(testEvent);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> eventService.delete(999L));

        verify(eventRepository, times(1)).findById(999L);
        verify(eventRepository, never()).delete(any());
    }
}