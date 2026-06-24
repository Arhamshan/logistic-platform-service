package com.logistic.platform.service.impl;

import com.logistic.common.entity.Notification;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.NotificationWriterRepository;
import com.logistic.platform.service.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LogManager.getLogger(NotificationServiceImpl.class);

    private final NotificationWriterRepository writerRepository;

    public NotificationServiceImpl(NotificationWriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    @Transactional
    public Notification save(Notification notification, String requestId, String username) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] saveNotification", requestId);

        Notification saved = null;

        try {
            LocalDateTime now = LocalDateTime.now();
            notification.setCreatedDate(now);
            notification.setUpdatedDate(now);
            notification.setCreatedBy(username);
            notification.setUpdatedBy(username);

            saved = writerRepository.save(notification, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] saveNotification: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] saveNotification: notificationId={}|timeTaken={}",
                    requestId, saved != null ? saved.getId() : null,
                    CommonUtils.getExecutionTime(startTime));
        }

        return saved;
    }
}