package com.sental.entity;

import com.sental.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String username;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}