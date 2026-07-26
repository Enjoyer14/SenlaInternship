package com.sental.dto.request;

public class TicketCreateRequest {
    private Long userId;
    private Long eventId;

    public TicketCreateRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}