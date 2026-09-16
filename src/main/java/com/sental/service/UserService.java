package com.sental.service;

import com.sental.dto.request.create.UserCreateRequest;
import com.sental.dto.request.update.UserUpdateRequest;
import com.sental.dto.response.UserResponse;
import com.sental.entity.User;
import com.sental.enums.UserRole;
import com.sental.exception.EntityNotFound;
import com.sental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(encodeAndWipe(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User not found: " + id));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<UserResponse> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            result.add(toResponse(user));
        }
        return result;
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User not found: " + id));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        char[] rawPassword = request.getPassword();
        if (rawPassword != null && rawPassword.length > 0) {
            user.setPasswordHash(encodeAndWipe(rawPassword));
        }
        user.setUpdatedAt(LocalDateTime.now());

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User not found: " + id));
        userRepository.delete(user);
    }

    private String encodeAndWipe(char[] rawPassword) {
        if (rawPassword == null || rawPassword.length == 0) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        try {
            return passwordEncoder.encode(CharBuffer.wrap(rawPassword));
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}