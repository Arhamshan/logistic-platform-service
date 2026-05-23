package com.logistic.platform.util;

public class EventQueryUtil {

    public static String insertEventQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Events\" (");
        query.append("     cons_item_id, ");
        query.append("     event_type, ");
        query.append("     event_location_code, ");
        query.append("     description, ");
        query.append("     created_date, ");
        query.append("     created_by, ");
        query.append("     updated_date, ");
        query.append("     updated_by ");
        query.append(" ) VALUES (");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ? ");
        query.append(" )");
        return query.toString();
    }

    public static String findEventsByItemIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     e.id, ");
        query.append("     e.cons_item_id, ");
        query.append("     e.event_type, ");
        query.append("     e.event_location_code, ");
        query.append("     e.description, ");
        query.append("     e.created_date, ");
        query.append("     e.created_by, ");
        query.append("     e.updated_date, ");
        query.append("     e.updated_by ");
        query.append(" FROM \"Events\" e ");
        query.append(" WHERE e.cons_item_id = ? ");
        query.append(" ORDER BY e.created_date DESC ");
        return query.toString();
    }

    public static String findTrackingEventsByItemIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     e.event_type, ");
        query.append("     e.description, ");
        query.append("     e.created_date, ");
        query.append("     l.name  AS location_name, ");
        query.append("     l.type  AS location_type, ");
        query.append("     l.location_code AS location_code ");
        query.append(" FROM \"Events\" e ");
        query.append(" INNER JOIN \"Locations\" l ");
        query.append("     ON e.event_location_code = l.location_code ");
        query.append(" WHERE e.cons_item_id = ? ");
        query.append(" ORDER BY e.created_date ASC ");
        return query.toString();
    }
}