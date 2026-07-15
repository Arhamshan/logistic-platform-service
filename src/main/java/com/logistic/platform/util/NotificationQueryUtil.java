package com.logistic.platform.util;

public class NotificationQueryUtil {

    public static String insertNotificationQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Notifications\" (");
        query.append("     cons_id, ");
        query.append("     event_code, ");
        query.append("     recipient_email, ");
        query.append("     recipient_phone, ");
        query.append("     subject, ");
        query.append("     message, ");
        query.append("     status, ");
        query.append("     sent_date, ");
        query.append("     created_date, ");
        query.append("     created_by, ");
        query.append("     updated_date, ");
        query.append("     updated_by ");
        query.append(" ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        return query.toString();
    }
}