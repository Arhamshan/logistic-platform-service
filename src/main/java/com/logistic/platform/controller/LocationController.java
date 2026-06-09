package com.logistic.platform.controller;


import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Location;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.location.GetAllLocationResponseDto;
import com.logistic.platform.dto.location.LocationRequestDto;
import com.logistic.platform.dto.location.LocationResponseDto;
import com.logistic.platform.dto.location.UpdateLocationRequestDto;
import com.logistic.platform.service.LocationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

        try {
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
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to create location.");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] createLocation: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<GetAllLocationResponseDto>>> getAllLocations(
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getAllLocations: ", requestId);

        ResponseDto<List<GetAllLocationResponseDto>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] getAllLocations: usernameFromAuthHeader={}", requestId, auth.getName());

            // Service returns List<Location> (entities), not DTOs
            List<Location> locations = service.getAllLocations(requestId);

            if (locations == null) {
                locations = List.of();
            }

            if(locations != null && !locations.isEmpty()) {
                // Convert entities to DTOs in the CONTROLLER layer
                List<GetAllLocationResponseDto> locationDtos = locations.stream()
                        .map(location -> new GetAllLocationResponseDto(
                                location.getId(),
                                location.getName(),
                                location.getLocationCode(),
                                location.getCountry(),
                                location.getCity(),
                                location.getType().toString(),
                                location.getLatitude(),
                                location.getLongitude()))
                        .toList();

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Locations fetched successfully");
                response.setData(locationDtos);

            } else {
                response.setResponseCode(HttpStatus.NOT_FOUND.value());
                response.setResponseMessage("No locations found");
                response.setData(List.of());
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getAllLocations: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to fetch locations.");
            response.setData(null);
        }

        response.setTimestamp(LocalDateTime.now());

        LOGGER.info("END [REST-LAYER] [RequestId={}] getAllLocations: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ResponseDto<Void>> updateLocation(
            @RequestBody UpdateLocationRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] updateLocation: requestBody={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {

            Location location = requestDto.toLocation();
            // No need to set ID separately as it comes from requestDto

            if (location.getName() == null || location.getType() == null || location.getCountry() == null ||
                    location.getCity() == null || location.getType() == null) {

                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to update location");
                response.setData(null);

            } else {

                Boolean isUpdated = service.updateLocation(location, requestId);

                if (Boolean.TRUE.equals(isUpdated)) {

                    // SUCCESS (200)
                    response.setResponseCode(HttpStatus.OK.value());
                    response.setResponseMessage("Location updated successfully");
                    response.setData(null);

                } else {

                    // NOT FOUND treated as 400
                    response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    response.setResponseMessage("Failed to update location");
                    response.setData(null);

                }

            }

        } catch (Exception e) {

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] updateLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            // INTERNAL ERROR (500)
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to update location");
            response.setData(null);

        } finally {
            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] updateLocation: responseBody={} | timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.status(HttpStatus.valueOf(response.getResponseCode())).body(response);
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<ResponseDto<GetAllLocationResponseDto>> getLocationByCode(
            @PathVariable("locationCode") String locationCode,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getLocationByCode: locationCode={}",
                requestId, locationCode);

        ResponseDto<GetAllLocationResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Location location = service.getLocationByCode(locationCode, requestId);  //return single Location

            if (location != null) {
                GetAllLocationResponseDto dto = new GetAllLocationResponseDto(location);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Location retrieved successfully");
                response.setData(dto);  // Setting single DTO, not List

            } else {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Location not found");
                response.setData(null);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getLocationByCode: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get location");
            response.setData(null);

        } finally {
            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] getLocationByCode: responseBody={} | timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(response),
                    CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.status(HttpStatus.valueOf(response.getResponseCode())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteLocation(
            @PathVariable("id") Long id,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] deleteLocation: id={}",
                requestId, id);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Boolean isDeleted = service.deleteById(id, requestId);

            if (Boolean.TRUE.equals(isDeleted)) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Location deleted successfully.");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to delete location.");
                response.setData(null);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to delete location.");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] deleteLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] deleteLocation: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }
}
