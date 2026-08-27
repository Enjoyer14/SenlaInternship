package com.sental.controller;

import com.sental.dto.request.update.VenueUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.VenueCreateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {
    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse create(@RequestBody VenueCreateRequest request) {
        return venueService.create(request);
    }

    @GetMapping("/{id}")
    public VenueResponse findById(@PathVariable("id") Long id) {
        return venueService.findById(id);
    }

    @GetMapping()
    public List<VenueResponse> findAll() {
        return venueService.findAll();
    }

    @PutMapping("/{id}")
    public VenueResponse update(@PathVariable("id") Long id, @RequestBody VenueUpdateRequest request) {
        return venueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        venueService.delete(id);
    }

}