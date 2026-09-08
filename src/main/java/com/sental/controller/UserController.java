package com.sental.controller;

import com.sental.dto.request.update.UserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.UserCreateRequest;
import com.sental.dto.response.UserResponse;
import com.sental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "UserController is working!";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}