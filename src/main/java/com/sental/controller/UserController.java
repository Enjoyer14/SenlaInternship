package com.sental.controller;

import com.sental.exception.EntityNotFound;
import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.UserCreateRequest;
import com.sental.dto.response.UserResponse;
import com.sental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public String create(UserCreateRequest request) {
        UserResponse response = userService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        try {
            UserResponse response = userService.findById(id);
            return toJson(response);
        } catch (EntityNotFound e) {
            return "404 Not Found " + e.getMessage(); // 404 на будущее
        }
    }

    public String findAll() {
        List<UserResponse> list = userService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        userService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}