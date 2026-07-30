package com.sental.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Organizer {
    private Long id;
    private Long userId;
    private String companyName;
    private String description;
    private String phone;
}