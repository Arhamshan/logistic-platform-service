package com.logistic.platform.service;

import com.logistic.common.entity.User;
import com.logistic.platform.dto.auth.LoginRequestDto;

public interface AuthService {

    public User login(String username, String password, String requestId);
}
