package com.sental.service;

import com.sental.dto.request.VenueCreateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.entity.Venue;
import com.sental.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public VenueResponse create(VenueCreateRequest request) {
        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setRegion(request.getRegion());
        venue.setCapacity(request.getCapacity());
        venue.setCreatedAt(LocalDateTime.now());

        Venue saved = venueRepository.save(venue);
        return toResponse(saved);
    }

    public VenueResponse findById(Long id) {
        Venue venue = venueRepository.findById(id).orElseThrow(() -> new RuntimeException("Venue not found: " + id));
        return toResponse(venue);
    }

    public List<VenueResponse> findAll() {
        List<VenueResponse> result = new ArrayList<>();
        for (Venue venue : venueRepository.findAll()) {
            result.add(toResponse(venue));
        }
        return result;
    }

    public void delete(Long id) {
        venueRepository.delete(id);
    }

    private VenueResponse toResponse(Venue venue) {
        VenueResponse response = new VenueResponse();
        response.setId(venue.getId());
        response.setName(venue.getName());
        response.setAddress(venue.getAddress());
        response.setCity(venue.getCity());
        response.setRegion(venue.getRegion());
        response.setCapacity(venue.getCapacity());
        response.setCreatedAt(venue.getCreatedAt());
        response.setUpdatedAt(venue.getUpdatedAt());
        return response;
    }
}