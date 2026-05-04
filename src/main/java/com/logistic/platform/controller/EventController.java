package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;
import com.logistic.common.util.CommonUtils;
import com.logistic.common.entity.Event;
import com.logistic.platform.dto.event.CreateEventRequestDto;
import com.logistic.platform.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/event")
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createEvent(
            @RequestBody CreateEventRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createEvent: request={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);
        response.setTimestamp(LocalDateTime.now());

        try {
            // 🔥 DTO → ENTITY mapping
            Event event = new Event();
            event.setEventType(EventType.valueOf(requestDto.getEventType()));
            event.setEventLocationCode(requestDto.getLocationCode());
            event.setDescription(requestDto.getDescription());

            // item mapping
            Item item = new Item();
            item.setItemId(requestDto.getItemId());

            // consignment id mapping
            Consignment consignment = new Consignment();
            consignment.setConsignmentId(requestDto.getConsignmentId());

            event.setItem(item);
            item.setConsignment(consignment);

            eventService.createEvent(event, requestId);

            // itemservice.getitembyconsignmentidAndItemId()
            // eventservice.saveEvent() - existing one
            // itemService.updateStatus()
            // consignmentService.updateStatus()





            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Event created successfully");
            response.setData(null);

            LOGGER.info("END [REST-LAYER] [RequestId={}] createEvent: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] {}", requestId, e.getMessage());

            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage("Failed to create event");
            response.setData(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createEvent failed", requestId, e);

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to create event");
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}