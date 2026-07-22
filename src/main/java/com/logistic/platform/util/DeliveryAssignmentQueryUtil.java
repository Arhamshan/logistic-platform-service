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

    public static String findSummaryQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     (SELECT COUNT(*) FROM \"Items\" ");
        query.append("      WHERE status = 'IN_TRANSIT') ");
        query.append("          AS total_intransit_items, ");
        query.append("     (SELECT COUNT(*) FROM \"DeliveryAssignments\" ");
        query.append("      WHERE assigned_datetime >= CURRENT_DATE ");
        query.append("      AND assigned_datetime <= CURRENT_DATE + INTERVAL '1 day') ");
        query.append("          AS today_assignments, ");
        query.append("     (SELECT COUNT(*) FROM \"Users\" ");
        query.append("      WHERE role = 'DRIVER') ");
        query.append("          AS total_drivers ");

        return query.toString();
    }
}