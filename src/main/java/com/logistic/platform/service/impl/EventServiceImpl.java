package com.logistic.platform.service.impl;

import com.logistic.common.entity.Event;
import com.logistic.platform.repository.writer.EventWriterRepository;
import com.logistic.platform.service.EventService;
import com.logistic.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);


    private final EventWriterRepository eventWriterRepository;

    public EventServiceImpl(EventWriterRepository eventWriterRepository) {
        this.eventWriterRepository = eventWriterRepository;
    }

    @Override
    @Transactional
    public Event saveEvent(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] saveEvent: event={}", requestId, CommonUtils.convertToString(event));

        Event savedEvent = null;

        try {
            if (event.getCreatedDate() == null) {
                event.setCreatedDate(LocalDateTime.now());
            }
            if (event.getUpdatedDate() == null) {
                event.setUpdatedDate(LocalDateTime.now());
            }

            savedEvent = eventWriterRepository.save(event, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] saveEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] saveEvent: eventId={} | timeTaken={}",
                requestId, savedEvent.getId(), CommonUtils.getExecutionTime(startTime));

        return savedEvent;
    }
}