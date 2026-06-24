package com.logistic.platform.repository;

import com.logistic.common.entity.Notification;

public interface NotificationRepository {

    public default Notification save(Notification notification, String requestId){
        return null;
    };
}
