package com.sental.dto.request.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String email;
    private String username;
    private char[] password;
}
