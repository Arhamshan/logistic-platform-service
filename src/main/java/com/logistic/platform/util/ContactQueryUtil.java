package com.logistic.platform.util;

public class ContactQueryUtil {
    public static String insertContactQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Contacts\" (");
        query.append("     name, ");
        query.append("     email, ");
        query.append("     phone, ");
        query.append("     address_line1, ");
        query.append("     address_line2, ");
        query.append("     state, ");
        query.append("     suburb, ");
        query.append("     postcode, ");
        query.append("     country, ");
        query.append("     latitude, ");
        query.append("     longitude, ");
        query.append("     created_date, ");
        query.append("     created_by, ");
        query.append("     updated_date, ");
        query.append("     updated_by ");
        query.append(" ) VALUES (");
        query.append("     ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
        query.append(" )");
        return query.toString();
    }

    public static String findContactByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     c.id, ");
        query.append("     c.name, ");
        query.append("     c.email, ");
        query.append("     c.phone, ");
        query.append("     c.address_line1, ");
        query.append("     c.address_line2, ");
        query.append("     c.state, ");
        query.append("     c.suburb, ");
        query.append("     c.postcode, ");
        query.append("     c.country, ");
        query.append("     c.latitude, ");
        query.append("     c.longitude, ");
        query.append("     c.created_date, ");
        query.append("     c.created_by, ");
        query.append("     c.updated_date, ");
        query.append("     c.updated_by ");
        query.append(" FROM \"Contacts\" c ");
        query.append(" WHERE c.id = ? ");
        return query.toString();
    }

    public static String updateContactQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE \"Contacts\" ");
        query.append(" SET name          = ?, ");
        query.append("     email         = ?, ");
        query.append("     phone         = ?, ");
        query.append("     address_line1 = ?, ");
        query.append("     address_line2 = ?, ");
        query.append("     state         = ?, ");
        query.append("     suburb        = ?, ");
        query.append("     postcode      = ?, ");
        query.append("     country       = ?, ");
        query.append("     updated_date  = ?, ");
        query.append("     updated_by    = ? ");
        query.append(" WHERE id = ? ");
        return query.toString();
    }

    public static String deleteContactByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" DELETE FROM \"Contacts\" ");
        query.append(" WHERE id = ? ");
        return query.toString();
    }
}
