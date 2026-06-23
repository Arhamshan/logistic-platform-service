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
        query.append("     id, ");
        query.append("     consignment_id, ");
        query.append("     status ");
        query.append(" FROM \"Consignments\" ");
        query.append(" WHERE consignment_id = ? ");

        return query.toString();
    }

    public static String findSummaryQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     COUNT(*) AS total_consignments, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'BOOKED')  AS booked, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'PICKED_UP')   AS picked_up, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'IN_TRANSIT')  AS in_transit, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'OUT_FOR_DELIVERY')  AS out_for_delivery, ");
        query.append("     COUNT(*) FILTER (WHERE status = 'DELIVERED') AS delivered, ");
        query.append("     COUNT(*) FILTER (WHERE DATE(created_date) = CURRENT_DATE) AS today_bookings ");
        query.append(" FROM \"Consignments\" ");

        return query.toString();
    }

    public static String findAllQuery(String sortBy, String sortDir) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     id, ");
        query.append("     consignment_id, ");
        query.append("     status, ");
        query.append("     created_date, ");
        query.append("     sender_contact_id, ");
        query.append("     destination_contact_id ");
        query.append(" FROM \"Consignments\" ");
        query.append(" ORDER BY " + sortBy + " " + sortDir.toUpperCase() + " ");
        query.append(" LIMIT ? ");
        query.append(" OFFSET ? ");

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

    public static String updateItemQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE \"Items\" ");
        query.append(" SET item_id              = COALESCE(?, item_id), ");
        query.append("     weight               = COALESCE(?, weight), ");
        query.append("     height               = COALESCE(?, height), ");
        query.append("     length               = COALESCE(?, length), ");
        query.append("     width                = COALESCE(?, width), ");
        query.append("     current_location_code = COALESCE(?, current_location_code), ");
        query.append("     updated_date         = ?, ");
        query.append("     updated_by           = ? ");
        query.append(" WHERE id = ? ");

        return query.toString();
    }
}