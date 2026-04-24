package com.logistic.platform.service;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;

import java.util.List;

public interface EventService {

    // Create/save a new event
    Event createEvent(Event event, String requestId);

    // Save event with full Event object
    Event saveEvent(Event event, String requestId);

}