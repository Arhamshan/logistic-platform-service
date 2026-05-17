package com.logistic.platform.repository;

import com.logistic.common.entity.Event;
import com.logistic.platform.vo.TrackingEventVo;

import java.util.List;

public interface EventRepository {

    public default Event save(Event event, String requestId){
        return null;
    };

    public default List<TrackingEventVo> findTrackingEventsByItemId(Long itemId, String requestId) {
        return null;
    };

    }