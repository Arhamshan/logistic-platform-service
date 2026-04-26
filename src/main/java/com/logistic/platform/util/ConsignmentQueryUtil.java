package com.logistic.platform.util;

public class ConsignmentQueryUtil {

    private ConsignmentQueryUtil() {
        throw new IllegalStateException("ConsignmentQueryUtil class");
    }

    public static String insertQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Consignments\" (");
        query.append("     consignment_id, ");
        query.append("     sender_contact_id, ");
        query.append("     destination_contact_id, ");
        query.append("     status, ");
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