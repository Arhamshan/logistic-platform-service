package com.logistic.platform.service;

import com.logistic.platform.dto.auth.AuthResponseDto;
import com.logistic.platform.dto.auth.LoginRequestDto;

public interface AuthService {

    public AuthResponseDto login(LoginRequestDto request, String requestId);
}
