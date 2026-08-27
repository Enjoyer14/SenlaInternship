package com.sental.dto.request.create;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueCreateRequest {
    private String name;
    private String address;
    private String city;
    private String region;
    private Integer capacity;

}