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
}
