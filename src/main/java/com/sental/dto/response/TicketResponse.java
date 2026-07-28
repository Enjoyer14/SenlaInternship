package com.sental.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sental.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private Long userId;
    private Long eventId;
    private TicketStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime paidAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime updatedAt;
}