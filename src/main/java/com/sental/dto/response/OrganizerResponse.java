package com.sental.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String description;
    private String phone;

}