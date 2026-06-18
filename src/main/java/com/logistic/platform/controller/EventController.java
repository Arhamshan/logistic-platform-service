package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.Location;
import com.logistic.common.enums.EventType;
import com.logistic.common.util.CommonUtils;
import com.logistic.common.entity.Event;
import com.logistic.platform.dto.event.CreateEventRequestDto;
import com.logistic.platform.repository.reader.LocationReaderRepository;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/event")
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final LocationService locationService;

    public EventController(EventService eventService,
                           LocationService locationService) {
        this.eventService = eventService;
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createEvent(
            @RequestBody CreateEventRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createEvent request={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);
        response.setTimestamp(LocalDateTime.now());

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Validate LocationCode
            Location location = locationService.getLocationByCode(requestDto.getLocationCode(), requestId);

            if (location != null) {

                Event event = new Event();
                event.setEventType(EventType.valueOf(requestDto.getEventType()));
                event.setEventLocationCode(requestDto.getLocationCode());
                event.setDescription(requestDto.getDescription());

                if (auth != null) {
                    event.setCreatedBy(auth.getName());
                }

                // item mapping
                Item item = new Item();
                item.setItemId(requestDto.getItemId());

                // consignment id mapping
                Consignment consignment = new Consignment();
                consignment.setConsignmentId(requestDto.getConsignmentId());

                item.setConsignment(consignment);
                event.setItem(item);

                eventService.createEvent(event, requestId);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Event created successfully");
                response.setData(null);

                LOGGER.info("END [REST-LAYER] [RequestId={}] createEvent: timeTaken={}",
                        requestId, CommonUtils.getExecutionTime(startTime));
            } else {

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Invalid locationCode");
                response.setData(null);
            }

        } catch (IllegalArgumentException e) {

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createEvent {}",
                    requestId, e.getMessage());

            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage("Failed to create event");
            response.setData(null);

        } catch (Exception e) {
            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createEvent Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to create event");
            response.setData(null);

        }finally {
            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] createEvent response={}|timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(response),
                    CommonUtils.getExecutionTime(startTime));
        }
        return ResponseEntity.ok(response);
    }
}