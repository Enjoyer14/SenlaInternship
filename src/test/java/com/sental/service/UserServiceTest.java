package com.sental.service;

import com.sental.dto.request.create.UserCreateRequest;
import com.sental.dto.request.update.UserUpdateRequest;
import com.sental.dto.response.UserResponse;
import com.sental.entity.User;
import com.sental.enums.UserRole;
import com.sental.exception.EntityNotFound;
import com.sental.repository.UserRepository;
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
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setEmail("new@example.com");
        createRequest.setUsername("newuser");
        createRequest.setPassword("newpass");

        updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("updated@example.com");
        updateRequest.setUsername("updateduser");
        updateRequest.setPassword("updatedpass");
    }

    @Test
    void create_shouldSaveAndReturnUserResponse() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        UserResponse response = userService.create(createRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getRole(), response.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findById_whenUserExists_shouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        UserResponse response = userService.findById(1L);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_whenUserNotFound_shouldThrowEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> userService.findById(99L));

        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void findAll_shouldReturnListOfUserResponses() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setUsername("another");
        anotherUser.setRole(UserRole.USER);
        anotherUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(testUser, anotherUser));

        List<UserResponse> responses = userService.findAll();
        assertEquals(2, responses.size());
        assertEquals(testUser.getId(), responses.get(0).getId());
        assertEquals(anotherUser.getId(), responses.get(1).getId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void update_whenUserExists_shouldUpdateAndReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.update(1L, updateRequest);
        assertNotNull(response);
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getUsername(), response.getUsername());
        assertNotNull(testUser.getUpdatedAt());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void update_whenUserNotFound_shouldThrowEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> userService.update(99L, updateRequest));

        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_whenUserExists_shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);
        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void delete_whenUserNotFound_shouldThrowEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> userService.delete(99L));

        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).delete(any());
    }
}