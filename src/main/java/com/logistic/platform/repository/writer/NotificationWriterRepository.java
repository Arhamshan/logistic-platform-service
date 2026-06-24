package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Notification;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.NotificationRepository;
import com.logistic.platform.util.NotificationQueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Map;

@Repository
public class NotificationWriterRepository implements NotificationRepository {

    private static final Logger LOGGER = LogManager.getLogger(NotificationWriterRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public NotificationWriterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Notification save(Notification notification, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] saveNotification: notification={}",
                requestId, CommonUtils.convertToString(notification));

        try {
            String sql = NotificationQueryUtil.insertNotificationQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1,  notification.getConsId());
                ps.setString(2,  notification.getEventCode());
                ps.setString(3,  notification.getRecipientEmail());
                ps.setString(4,  notification.getRecipientPhone());
                ps.setString(5,  notification.getSubject());
                ps.setString(6,  notification.getMessage());
                ps.setString(7,  notification.getStatus());
                ps.setTimestamp(8, notification.getSentDate() != null
                        ? Timestamp.valueOf(notification.getSentDate()) : null);
                ps.setTimestamp(9, notification.getCreatedDate() != null
                        ? Timestamp.valueOf(notification.getCreatedDate()) : null);
                ps.setString(10, notification.getCreatedBy());
                ps.setTimestamp(11, notification.getUpdatedDate() != null
                        ? Timestamp.valueOf(notification.getUpdatedDate()) : null);
                ps.setString(12, notification.getUpdatedBy());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.get("id") != null) {
                notification.setId(((Number) keys.get("id")).longValue());
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] saveNotification: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save notification", e);
        }

        LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] saveNotification: notificationId={}|timeTaken={}",
                requestId, notification.getId(), CommonUtils.getExecutionTime(startTime));

        return notification;
    }
}