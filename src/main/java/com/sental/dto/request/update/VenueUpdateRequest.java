package com.sental.dto.request.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueUpdateRequest {
    private String name;
    private String address;
    private String city;
    private String region;
    private Integer capacity;
}
