package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.auth.AuthResponseDto;
import com.logistic.platform.dto.auth.LoginRequestDto;
import com.logistic.platform.service.AuthService;
import com.logistic.platform.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<AuthResponseDto>> login(
            @RequestBody LoginRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] login: requestBody={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<AuthResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {

            User user = authService.login(requestDto.getUsername(), requestDto.getPassword(), requestId);

            if (user != null) {
                AuthResponseDto login = new AuthResponseDto(user.getToken(), user.getUsername(), user.getRole().name());

                response.setData(login);
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Logged in successfully");

            } else {
                response.setResponseCode(HttpStatus.NOT_FOUND.value());
                response.setResponseMessage("Login not found");
            }

        } catch (Exception e) {

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] login: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Login failed");

        } finally {

            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] login: responseBody={} | timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(response),
                    CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.status(HttpStatus.valueOf(response.getResponseCode())).body(response);
    }
}
