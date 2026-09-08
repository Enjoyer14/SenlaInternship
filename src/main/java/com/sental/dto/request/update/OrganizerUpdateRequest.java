package com.sental.dto.request.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizerUpdateRequest {
    private String companyName;
    private String description;
    private String phone;
}