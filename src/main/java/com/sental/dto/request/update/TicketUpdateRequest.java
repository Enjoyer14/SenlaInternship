package com.sental.dto.request.update;

import com.sental.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketUpdateRequest {
    private TicketStatus status;
}