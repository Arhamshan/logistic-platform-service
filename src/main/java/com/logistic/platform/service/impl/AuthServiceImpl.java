package com.logistic.platform.service.impl;

import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
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
    public User login(String username, String password, String requestId) {
        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] login: username={}",
                requestId, username);

        // This throws an exception if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        );

        User user = userService.getByUsername(username, requestId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        user.setToken(token);

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] login: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return user;
    }
}