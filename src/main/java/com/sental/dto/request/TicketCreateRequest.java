package com.sental.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketCreateRequest {
    private Long userId;
    private Long eventId;

}