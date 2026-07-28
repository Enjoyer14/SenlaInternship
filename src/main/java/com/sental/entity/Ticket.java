package com.sental.entity;

import com.sental.enums.TicketStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    private Long id;
    private String ticketNumber;
    private Long userId;
    private Long eventId;
    private TicketStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime updatedAt;

}