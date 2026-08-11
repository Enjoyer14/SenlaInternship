package com.sental.entity;

import com.sental.enums.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private Long venueId;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private Integer capacity;
    private Integer bookedSeats;
    private Double price;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}