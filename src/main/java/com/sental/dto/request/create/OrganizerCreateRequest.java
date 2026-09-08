package com.sental.dto.request.create;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizerCreateRequest {
    private Long userId;
    private String companyName;
    private String description;
    private String phone;

}