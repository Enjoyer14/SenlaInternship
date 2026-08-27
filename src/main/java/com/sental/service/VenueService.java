package com.sental.service;

import com.sental.dto.request.create.VenueCreateRequest;
import com.sental.dto.request.update.VenueUpdateRequest;
import com.sental.dto.response.VenueResponse;
import com.sental.entity.Venue;
import com.sental.exception.EntityNotFound;
import com.sental.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    @Transactional(readOnly = true)
    public VenueResponse findById(Long id) {
        Venue venue = venueRepository.findById(id).orElseThrow(() -> new EntityNotFound("Venue not found: " + id));
        return toResponse(venue);
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> findAll() {
        List<VenueResponse> result = new ArrayList<>();
        for (Venue venue : venueRepository.findAll()) {
            result.add(toResponse(venue));
        }
        return result;
    }

    @Transactional
    public VenueResponse update(Long id, VenueUpdateRequest request) {
        Venue venue = venueRepository.findById(id).orElseThrow(() -> new EntityNotFound("Venue not found: " + id));

        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setRegion(request.getRegion());
        venue.setCapacity(request.getCapacity());
        venue.setUpdatedAt(LocalDateTime.now());

        Venue updated = venueRepository.save(venue);

        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Venue venue = venueRepository.findById(id).orElseThrow(() -> new EntityNotFound("Venue not found: " + id));
        venueRepository.delete(venue);
    }

    private VenueResponse toResponse(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .city(venue.getCity())
                .region(venue.getRegion())
                .capacity(venue.getCapacity())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build();
    }
}