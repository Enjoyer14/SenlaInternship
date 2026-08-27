package com.sental.service;

import com.sental.dto.request.create.OrganizerCreateRequest;
import com.sental.dto.request.update.OrganizerUpdateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.entity.Organizer;
import com.sental.entity.User;
import com.sental.exception.EntityNotFound;
import com.sental.repository.OrganizerRepository;
import com.sental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizerServiceTest {
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private OrganizerService organizerService;

    private User testUser;
    private Organizer testOrganizer;
    private OrganizerCreateRequest createRequest;
    private OrganizerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("organizer_user");

        testOrganizer = new Organizer();
        testOrganizer.setId(10L);
        testOrganizer.setUser(testUser);
        testOrganizer.setCompanyName("Acme Corp");
        testOrganizer.setDescription("Event organizer");
        testOrganizer.setPhone("+123456789");

        createRequest = new OrganizerCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setCompanyName("New Company");
        createRequest.setDescription("New desc");
        createRequest.setPhone("+987654321");

        updateRequest = new OrganizerUpdateRequest();
        updateRequest.setCompanyName("Updated Company");
        updateRequest.setDescription("Updated desc");
        updateRequest.setPhone("+111111111");
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizerRepository.save(any(Organizer.class))).thenAnswer(invocation -> {
            Organizer saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        OrganizerResponse response = organizerService.create(createRequest);
        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("New Company", response.getCompanyName());
        assertEquals("New desc", response.getDescription());
        assertEquals("+987654321", response.getPhone());

        verify(userRepository, times(1)).findById(1L);
        verify(organizerRepository, times(1)).save(any(Organizer.class));
    }

    @Test
    void create_withInvalidUserId_shouldThrowEntityNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setUserId(999L);
        assertThrows(EntityNotFound.class, () -> organizerService.create(createRequest));

        verify(userRepository, times(1)).findById(999L);
        verify(organizerRepository, never()).save(any());
    }

    @Test
    void findById_whenExists_shouldReturnResponse() {
        when(organizerRepository.findById(10L)).thenReturn(Optional.of(testOrganizer));

        OrganizerResponse response = organizerService.findById(10L);
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("Acme Corp", response.getCompanyName());

        verify(organizerRepository, times(1)).findById(10L);
    }

    @Test
    void findById_whenNotFound_shouldThrowEntityNotFound() {
        when(organizerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> organizerService.findById(99L));

        verify(organizerRepository, times(1)).findById(99L);
    }

    @Test
    void findAll_shouldReturnListOfResponses() {
        Organizer another = new Organizer();
        another.setId(11L);
        another.setUser(testUser);
        another.setCompanyName("Another Corp");
        when(organizerRepository.findAll()).thenReturn(List.of(testOrganizer, another));

        List<OrganizerResponse> responses = organizerService.findAll();
        assertEquals(2, responses.size());
        assertEquals("Acme Corp", responses.get(0).getCompanyName());
        assertEquals("Another Corp", responses.get(1).getCompanyName());

        verify(organizerRepository, times(1)).findAll();
    }

    @Test
    void update_shouldModifyAndReturnResponse() {
        when(organizerRepository.findById(10L)).thenReturn(Optional.of(testOrganizer));
        when(organizerRepository.save(any(Organizer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizerResponse response = organizerService.update(10L, updateRequest);
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Updated Company", response.getCompanyName());
        assertEquals("Updated desc", response.getDescription());
        assertEquals("+111111111", response.getPhone());
        assertEquals(1L, response.getUserId());

        verify(organizerRepository, times(1)).findById(10L);
        verify(organizerRepository, times(1)).save(testOrganizer);
    }

    @Test
    void update_whenNotFound_shouldThrowEntityNotFound() {
        when(organizerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> organizerService.update(99L, updateRequest));

        verify(organizerRepository, times(1)).findById(99L);
        verify(organizerRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldCallRepositoryDelete() {
        when(organizerRepository.findById(10L)).thenReturn(Optional.of(testOrganizer));
        doNothing().when(organizerRepository).delete(testOrganizer);
        organizerService.delete(10L);

        verify(organizerRepository, times(1)).findById(10L);
        verify(organizerRepository, times(1)).delete(testOrganizer);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFound() {
        when(organizerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> organizerService.delete(99L));

        verify(organizerRepository, times(1)).findById(99L);
        verify(organizerRepository, never()).delete(any());
    }
}