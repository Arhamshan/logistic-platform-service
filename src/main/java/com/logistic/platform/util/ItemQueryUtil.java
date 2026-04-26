package com.logistic.platform.util;

public class ItemQueryUtil {

    public static String insertItemQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Items\" (");
        query.append("     item_id, ");
        query.append("     cons_id, ");
        query.append("     status, ");
        query.append("     current_location_code, ");
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

}