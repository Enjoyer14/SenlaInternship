package com.sental.controller;

import com.sental.dto.request.update.OrganizerUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.OrganizerCreateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {
    private final OrganizerService organizerService;

    @Autowired
    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizerResponse create(@RequestBody OrganizerCreateRequest request) {
        return organizerService.create(request);
    }

    @GetMapping("/{id}")
    public OrganizerResponse findById(@PathVariable("id") Long id) {
        return organizerService.findById(id);

    }

    @GetMapping
    public List<OrganizerResponse> findAll() {
        return organizerService.findAll();
    }

    @PutMapping("/{id}")
    public OrganizerResponse update(@PathVariable("id") Long id, @RequestBody OrganizerUpdateRequest request) {
        return organizerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        organizerService.delete(id);
    }

}