package com.sental.service;

import com.sental.dto.request.UserCreateRequest;
import com.sental.dto.response.UserResponse;
import com.sental.entity.User;
import com.sental.enums.UserRole;
import com.sental.exception.EntityNotFound;
import com.sental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserCreateRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFound("User not found: " + id));
        return toResponse(user);
    }

    public List<UserResponse> findAll() {
        List<UserResponse> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            result.add(toResponse(user));
        }
        return result;
    }

    public void delete(Long id) {
        userRepository.delete(id);
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