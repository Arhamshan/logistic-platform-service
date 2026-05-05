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

    public static String updateConsignmentStatusQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" UPDATE \"Consignments\" ");
        query.append(" SET status = ?, ");
        query.append("     updated_date = ?, ");
        query.append("     updated_by = ? ");
        query.append(" WHERE id = ? ");

        return query.toString();
    }

    public static String findConsignmentByIdQuery() {

        StringBuilder query = new StringBuilder();

        query.append(" SELECT ");
        query.append("     id, ");
        query.append("     consignment_id, ");
        query.append("     status, ");
        query.append("     created_date, ");
        query.append("     created_by, ");
        query.append("     updated_date, ");
        query.append("     updated_by ");
        query.append(" FROM \"Consignments\" ");
        query.append(" WHERE consignment_id = ? ");

        return query.toString();
    }
}