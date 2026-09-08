package com.sental.dto.request.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventUpdateRequest {
    private Long venueId;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private Integer capacity;
    private Double price;
    private List<Long> categoryIds;
}
