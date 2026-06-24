package com.logistic.platform.service;

import com.logistic.common.entity.Notification;

public interface NotificationService {
    Notification save(Notification notification, String requestId, String username);
}