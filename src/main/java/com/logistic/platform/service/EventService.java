package com.logistic.platform.service;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;
import com.logistic.platform.dto.event.CreateEventRequestDto;

import java.util.List;

public interface EventService {

    // Save event with full Event object
    Event saveEvent(Event event, String requestId);

    // Save event with individual parameters like status update
    void createEvent(Event event, String requestId);

}