package com.logistic.platform.service.impl;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;
import com.logistic.platform.repository.EventRepository;
import com.logistic.platform.service.EventService;
import com.logistic.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createEvent: itemId={}, eventType={}",
                requestId, event.getItem() != null ? event.getItem().getId() : null, event.getEventType());

        try {
            // Set default values if not present
            if (event.getCreatedDate() == null) {
                event.setCreatedDate(LocalDateTime.now());
            }
            if (event.getUpdatedDate() == null) {
                event.setUpdatedDate(LocalDateTime.now());
            }

            Event savedEvent = eventRepository.save(event, requestId);

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createEvent: eventId={} | timeTaken={}",
                    requestId, savedEvent.getId(), CommonUtils.getExecutionTime(startTime));

            return savedEvent;

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }
    }

    @Override
    @Transactional
    public Event saveEvent(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] saveEvent: itemId={}, eventType={}",
                requestId, event.getItem() != null ? event.getItem().getId() : null, event.getEventType());

        try {
            if (event.getCreatedDate() == null) {
                event.setCreatedDate(LocalDateTime.now());
            }
            if (event.getUpdatedDate() == null) {
                event.setUpdatedDate(LocalDateTime.now());
            }

            Event savedEvent = eventRepository.save(event, requestId);

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] saveEvent: eventId={} | timeTaken={}",
                    requestId, savedEvent.getId(), CommonUtils.getExecutionTime(startTime));

            return savedEvent;

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] saveEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }
    }
}