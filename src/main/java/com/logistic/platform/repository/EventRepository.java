package com.logistic.platform.repository;

import com.logistic.common.entity.Event;
import java.util.List;

public interface EventRepository {

    Event save(Event event, String requestId);

}