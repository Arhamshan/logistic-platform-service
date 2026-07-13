package com.logistic.platform.util;

public class UserQueryUtil {

    private UserQueryUtil() {
        throw new IllegalStateException("LocationQueryUtil class");
    }

    public static String createUserQuery() {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(" \"Users\" ");
        query.append(" (username, password, role, status, created_date, created_by, updated_date, updated_by) ");
        query.append("VALUES ");
        query.append(" (?, ?, ?, ?, ?, ?, ?, ?) ");

        return query.toString();
    }

    public static String findUserByUsernameQuery() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(" u.username, ");
        query.append(" u.password, ");
        query.append(" u.role, ");
        query.append(" u.status ");
        query.append("FROM ");
        query.append(" \"Users\" u ");
        query.append("WHERE ");
        query.append(" u.username = ? ");
        query.append(" AND u.status = 'ACTIVE' ");

        return query.toString();
    }

    public static String findAllByRoleQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     u.id, ");
        query.append("     u.username, ");
        query.append("     u.role, ");
        query.append("     u.status, ");
        query.append("     c.name    AS contact_name, ");
        query.append("     c.phone   AS contact_phone, ");
        query.append("     c.address_line1 AS contact_address ");
        query.append(" FROM \"Users\" u ");
        query.append(" LEFT JOIN \"Contacts\" c ON u.contact_id = c.id ");
        query.append(" WHERE u.role = ? ");
        return query.toString();
    }
}
