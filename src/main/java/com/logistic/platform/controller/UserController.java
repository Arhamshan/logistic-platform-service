package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.logistic.platform.dto.user.CreateUserRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<?>> createUser(
            @RequestBody CreateUserRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createUser: requestBody={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<?> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {

            // change to enum role and change user object type
            User user = requestDto.toUser();

            if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {

                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Invalid request: username, password, and role are required");
                response.setData(null);

            } else {

                Boolean isCreated = userService.createUser(user, requestId);

                if (Boolean.TRUE.equals(isCreated)) {

                    response.setResponseCode(HttpStatus.OK.value());
                    response.setResponseMessage("User created successfully");

                } else {

                    response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    response.setResponseMessage("Failed to create user");
                }
            }

        } catch (Exception e) {

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createUser: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to create user");

        } finally {

            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] createUser: responseBody={} | timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(response),
                    CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.status(HttpStatus.valueOf(response.getResponseCode())).body(response);
    }
}
