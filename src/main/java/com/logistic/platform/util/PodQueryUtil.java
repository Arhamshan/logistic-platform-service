package com.logistic.platform.util;

public class PodQueryUtil {

    private PodQueryUtil() {
        throw new IllegalStateException("PodQueryUtil class");
    }

    public static String insertPodQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" INSERT INTO \"Pods\" (");
        query.append("     cons_item_id, ");
        query.append("     received_by, ");
        query.append("     receiver_contact, ");
        query.append("     remarks, ");
        query.append("     pod_path, ");
        query.append("     delivered_at, ");
        query.append("     delivered_by, ");
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
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ? ");
        query.append(" )");
        return query.toString();
    }

    public static String findByConsItemIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     id, ");
        query.append("     received_by, ");
        query.append("     receiver_contact, ");
        query.append("     pod_path, ");
        query.append("     delivered_by ");
        query.append(" FROM \"Pods\" ");
        query.append(" WHERE cons_item_id = ? ");
        return query.toString();
    }
}