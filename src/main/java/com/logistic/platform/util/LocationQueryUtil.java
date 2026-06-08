package com.logistic.platform.util;

public class LocationQueryUtil {

    private LocationQueryUtil() {
        throw new IllegalStateException("LocationQueryUtil class");
    }


    public static String insertQuery() {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO \"Locations\"");
        query.append("(");
        query.append("location_code, ");
        query.append("name, ");
        query.append("type, ");
        query.append("city, ");
        query.append("country, ");
        query.append("latitude, ");
        query.append("longitude, ");
        query.append("created_date, ");
        query.append("created_by, ");
        query.append("updated_date, ");
        query.append("updated_by ");
        query.append(") ");
        query.append("VALUES (");
        query.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        query.append(") ");

        return query.toString();
    }

    public static String findLastLocationCodeQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" l.location_code ");
        query.append("FROM ");
        query.append(" \"Locations\" l ");
        query.append("ORDER BY id DESC ");
        query.append("LIMIT 1 ");

        return query.toString();
    }

    public static String findAllLocationsQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" l.id, ");
        query.append(" l.name, ");
        query.append(" l.location_code, ");
        query.append(" l.country, ");
        query.append(" l.type, ");
        query.append(" l.city, ");
        query.append(" l.longitude, ");
        query.append(" l.latitude ");
        query.append("FROM ");
        query.append(" \"Locations\" l ");

        return query.toString();
    }

    public static String updateLocationQuery() {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append("\"Locations\" ");
        query.append("SET ");
        query.append("name = ?, ");
        query.append("type = ?, ");
        query.append("country = ?, ");
        query.append("city = ?, ");
        query.append("latitude = ?, ");
        query.append("longitude = ? ");
        query.append("WHERE ");
        query.append("id = ?");

        return query.toString();
    }

    public static String findLocationByCodeQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" l.id, ");
        query.append(" l.name, ");
        query.append(" l.location_code, ");
        query.append(" l.country, ");
        query.append(" l.type, ");
        query.append(" l.city, ");
        query.append(" l.longitude, ");
        query.append(" l.latitude ");
        query.append("FROM ");
        query.append(" \"Locations\" l ");
        query.append("WHERE ");
        query.append(" l.location_code = ? ");

        return query.toString();
    }

    public static String deleteByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append("\"Locations\" ");
        query.append("WHERE ");
        query.append("id = ?");

        return query.toString();
    }
}
