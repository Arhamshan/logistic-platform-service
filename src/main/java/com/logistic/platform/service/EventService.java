package com.logistic.platform.service;

import com.logistic.common.entity.Event;
import com.logistic.platform.vo.TrackingEventVo;

import java.util.List;

public interface EventService {

    // Save event with full Event object
    Event saveEvent(Event event, String requestId);

    // Save event with individual parameters like status update
    void createEvent(Event event, String requestId);

    List<TrackingEventVo> getTrackingEvents(Long itemId, String requestId);

}