package com.logistic.platform.repository;

import com.logistic.common.entity.Event;
import java.util.List;

public interface EventRepository {

    public default Event save(Event event, String requestId){
        return null;
    };

    public default List<Event> findByItemId(String itemId, String requestId){
            return null;
    };

    public default List<Event> findByConsignmentId(String consignmentId, String requestId){
        return null;
    };
}