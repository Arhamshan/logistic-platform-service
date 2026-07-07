package com.logistic.platform.util;

public class DeliveryAssignmentQueryUtil {

    public static String insertDeliveryAssignmentQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"DeliveryAssignments\" ( ");
        query.append("     cons_item_id, ");
        query.append("     driver_user_id, ");
        query.append("     assigned_by, ");
        query.append("     assigned_datetime, ");
        query.append("     status, ");
        query.append("     remarks, ");
        query.append("     created_date, ");
        query.append("     updated_date ");
        query.append(" ) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

        return query.toString();
    }
}