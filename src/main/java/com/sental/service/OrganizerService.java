package com.sental.service;

import com.sental.dto.request.OrganizerCreateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.entity.Organizer;
import com.sental.exception.EntityNotFound;
import com.sental.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    @Autowired
    public OrganizerService(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    public OrganizerResponse create(OrganizerCreateRequest request) {
        Organizer organizer = new Organizer();
        organizer.setUserId(request.getUserId());
        organizer.setCompanyName(request.getCompanyName());
        organizer.setDescription(request.getDescription());
        organizer.setPhone(request.getPhone());

        Organizer saved = organizerRepository.save(organizer);
        return toResponse(saved);
    }

    public OrganizerResponse findById(Long id) {
        Organizer organizer = organizerRepository.findById(id).orElseThrow(() -> new EntityNotFound("Organizer not found: " + id));
        return toResponse(organizer);
    }

    public List<OrganizerResponse> findAll() {
        List<OrganizerResponse> result = new ArrayList<>();
        for (Organizer organizer : organizerRepository.findAll()) {
            result.add(toResponse(organizer));
        }
        return result;
    }

    public void delete(Long id) {
        organizerRepository.delete(id);
    }

    private OrganizerResponse toResponse(Organizer organizer) {
        return OrganizerResponse.builder()
                .id(organizer.getId())
                .userId(organizer.getUserId())
                .companyName(organizer.getCompanyName())
                .description(organizer.getDescription())
                .phone(organizer.getPhone())
                .build();
    }
}