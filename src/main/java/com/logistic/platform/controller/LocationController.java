package com.logistic.platform.controller;


import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Location;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.location.LocationRequestDto;
import com.logistic.platform.dto.location.LocationResponseDto;
import com.logistic.platform.service.LocationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/location")
public class LocationController {

    private static final Logger LOGGER = LogManager.getLogger(LocationController.class);

    final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<ResponseDto<LocationResponseDto>> createLocation(
            @RequestBody LocationRequestDto locationRequestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createLocation: ", requestId);

        ResponseDto<LocationResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        LocationResponseDto responseDto = null;

        Location location = locationRequestDto.toLocation();

        String newLocationCode = service.generateLocationCode(requestId);
        location.setLocationCode(newLocationCode);

        Boolean isCreated = service.create(location, requestId);

        if (Boolean.TRUE.equals(isCreated)) {
            responseDto = new LocationResponseDto(location.getName(), newLocationCode);

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Location created successfully.");
            response.setData(responseDto);

        } else {
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage("Failed to create location.");
        }

        response.setTimestamp(LocalDateTime.now());

        LOGGER.info("END [REST-LAYER] [RequestId={}] createLocation: response={}|timeTaken={}", requestId, response, CommonUtils.getExecutionTime(startTime));

        return ResponseEntity.ok(response);
    }
}
