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

    public static String findByConsignmentIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     c.id, ");
        query.append("     c.consignment_id, ");
        query.append("     c.status, ");

        query.append("     sc.name             AS sender_name, ");
        query.append("     sc.email            AS sender_email, ");
        query.append("     sc.phone            AS sender_phone, ");
        query.append("     sc.address_line1    AS sender_address_line1, ");
        query.append("     sc.address_line2    AS sender_address_line2, ");
        query.append("     sc.state            AS sender_state, ");
        query.append("     sc.suburb           AS sender_suburb, ");
        query.append("     sc.postcode         AS sender_postcode, ");
        query.append("     sc.country          AS sender_country, ");

        query.append("     dc.name             AS dest_name, ");
        query.append("     dc.email            AS dest_email, ");
        query.append("     dc.phone            AS dest_phone, ");
        query.append("     dc.address_line1    AS dest_address_line1, ");
        query.append("     dc.address_line2    AS dest_address_line2, ");
        query.append("     dc.state            AS dest_state, ");
        query.append("     dc.suburb           AS dest_suburb, ");
        query.append("     dc.postcode         AS dest_postcode, ");
        query.append("     dc.country          AS dest_country ");

        query.append(" FROM \"Consignments\" c ");
        query.append(" LEFT JOIN \"Contacts\" sc ON c.sender_contact_id = sc.id ");
        query.append(" LEFT JOIN \"Contacts\" dc ON c.destination_contact_id = dc.id ");
        query.append(" WHERE c.consignment_id = ? ");

        return query.toString();
    }

    public static String findSummaryQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     COUNT(*) AS total_consignments, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'BOOKED')  AS booked, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'PICKED_UP' OR status = 'PARTIALLY_PICKED_UP')   AS picked_up, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'IN_TRANSIT' OR status = 'PARTIALLY_IN_TRANSIT')  AS in_transit, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'OUT_FOR_DELIVERY' OR status = 'PARTIALLY_OUT_FOR_DELIVERY')  AS out_for_delivery, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'DELIVERED' OR status = 'PARTIALLY_DELIVERED') AS delivered, ");
        query.append("     COUNT(*) FILTER (WHERE DATE(created_date) = CURRENT_DATE) AS today_bookings ");
        query.append(" FROM \"Consignments\" ");

        return query.toString();
    }

    public static String findAllQuery(String sortBy, String sortDir, Boolean isCount) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");

        if (Boolean.TRUE.equals(isCount)) {
            query.append("     count(*) ");
        } else {
            query.append("     id, ");
            query.append("     consignment_id, ");
            query.append("     status, ");
            query.append("     created_date, ");
            query.append("     sender_contact_id, ");
            query.append("     destination_contact_id ");

        }
        query.append(" FROM \"Consignments\" ");

        if (Boolean.FALSE.equals(isCount)) {
            query.append(" ORDER BY " + sortBy + " " + sortDir.toUpperCase() + " ");
            query.append(" LIMIT ? ");
            query.append(" OFFSET ? ");
        }

        return query.toString();
    }

    public static String deleteByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" DELETE FROM ");
        query.append("  \"Consignments\" ");
        query.append(" WHERE ");
        query.append(" id = ? ");

        return query.toString();
    }

    public static String findByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     c.id, ");
        query.append("     c.consignment_id, ");
        query.append("     c.status, ");
        query.append("     c.sender_contact_id, ");
        query.append("     c.destination_contact_id, ");
        query.append("     i.id        AS item_pk, ");
        query.append("     i.item_id, ");
        query.append("     i.status    AS item_status, ");
        query.append("     i.current_location_code ");
        query.append(" FROM \"Consignments\" c ");
        query.append(" LEFT JOIN \"Items\" i ");
        query.append("     ON i.cons_id = c.id ");
        query.append(" WHERE c.id = ? ");

        return query.toString();
    }

    public static String updateConsignmentQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE \"Consignments\" ");
        query.append(" SET consignment_id         = COALESCE(?, consignment_id), ");
        query.append("     updated_date           = ?, ");
        query.append("     updated_by             = ? ");
        query.append(" WHERE id = ? ");

        return query.toString();
    }
}