package com.sental.service;

import com.sental.dto.request.OrganizerCreateRequest;
import com.sental.dto.response.OrganizerResponse;
import com.sental.entity.Organizer;
import com.sental.entity.User;
import com.sental.exception.EntityNotFound;
import com.sental.repository.OrganizerRepository;
import com.sental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrganizerService(OrganizerRepository organizerRepository, UserRepository userRepository) {
        this.organizerRepository = organizerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrganizerResponse create(OrganizerCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFound("User not found: " + request.getUserId()));

        Organizer organizer = new Organizer();
        organizer.setUser(user);
        organizer.setCompanyName(request.getCompanyName());
        organizer.setDescription(request.getDescription());
        organizer.setPhone(request.getPhone());

        Organizer saved = organizerRepository.save(organizer);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrganizerResponse findById(Long id) {
        Organizer organizer = organizerRepository.findById(id).orElseThrow(() -> new EntityNotFound("Organizer not found: " + id));
        return toResponse(organizer);
    }

    @Transactional(readOnly = true)
    public List<OrganizerResponse> findAll() {
        List<OrganizerResponse> result = new ArrayList<>();
        for (Organizer organizer : organizerRepository.findAll()) {
            result.add(toResponse(organizer));
        }
        return result;
    }

    @Transactional
    public void delete(Long id) {
        organizerRepository.deleteById(id);
    }

    private OrganizerResponse toResponse(Organizer organizer) {
        return OrganizerResponse.builder()
                .id(organizer.getId())
                .userId(organizer.getUser() == null ? null : organizer.getUser().getId())
                .companyName(organizer.getCompanyName())
                .description(organizer.getDescription())
                .phone(organizer.getPhone())
                .build();
    }
}