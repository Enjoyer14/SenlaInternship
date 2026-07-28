package com.sental.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sental.enums.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private Long venueId;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate;
    private Integer capacity;
    private Integer bookedSeats;
    private Double price;
    private EventStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

}