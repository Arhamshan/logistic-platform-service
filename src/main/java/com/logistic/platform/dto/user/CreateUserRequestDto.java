package com.logistic.platform.dto.user;

import com.logistic.common.entity.User;

import com.logistic.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    private String username;
    private String password;
    private String role;

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRole(Role.valueOf(this.role.toUpperCase()));

        return user;
    }
}