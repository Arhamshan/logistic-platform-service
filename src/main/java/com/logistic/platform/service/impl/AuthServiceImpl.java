package com.logistic.platform.service.impl;

import com.logistic.common.entity.User;
import com.logistic.common.enums.Status;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.auth.AuthResponseDto;
import com.logistic.platform.dto.auth.LoginRequestDto;
import com.logistic.platform.repository.reader.UserReaderRepository;
import com.logistic.platform.repository.writer.UserWriterRepository;
import com.logistic.platform.service.AuthService;
import com.logistic.platform.service.UserService;
import com.logistic.platform.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;


    public AuthServiceImpl(UserService userService,
                           AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Login and return JWT
    public AuthResponseDto login(LoginRequestDto request, String requestId) {
        // This throws an exception if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        User user = userService.getByUsername(request.getUsername(), requestId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponseDto(token, user.getUsername(), user.getRole().name());
    }
}