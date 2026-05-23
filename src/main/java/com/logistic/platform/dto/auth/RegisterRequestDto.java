package com.logistic.platform.dto.auth;

import com.logistic.common.enums.Role;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;

    private String password;

    private Role role;
}
