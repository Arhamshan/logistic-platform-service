package com.logistic.platform.util;

public class ItemQueryUtil {

    public static String insertItemQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO \"Items\" (");
        query.append("     item_id, ");
        query.append("     cons_id, ");
        query.append("     status, ");
        query.append("     current_location_code, ");
        query.append("     barcode_number, ");
        query.append("     weight, ");
        query.append("     height, ");
        query.append("     length, ");
        query.append("     width, ");
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
        query.append("     ?, ");
        query.append("     ?, ");
        query.append("     ? ");
        query.append(" )");
        return query.toString();
    }


    public static String updateItemStatusAndLocationQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" UPDATE \"Items\" ");
        query.append(" SET status = ?, ");
        query.append("     current_location_code = ?, ");
        query.append("     updated_date = ?, ");
        query.append("     updated_by = ? ");
        query.append(" WHERE id = ? ");

        return query.toString();
    }

    public static String findByItemIdAndConsignmentIdQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.cons_id, ");
        query.append("     i.status, ");
        query.append("     i.current_location_code, ");
        query.append("     i.created_date, ");
        query.append("     i.created_by, ");
        query.append("     i.updated_date, ");
        query.append("     i.updated_by, ");
        query.append("     c.id  AS consignment_pk, ");
        query.append("     c.consignment_id ");
        query.append(" FROM \"Items\" i ");
        query.append(" INNER JOIN \"Consignments\" c ");
        query.append("     ON i.cons_id = c.id ");
        query.append(" WHERE i.item_id = ? ");
        query.append(" AND c.consignment_id = ? ");

        return query.toString();
    }

    public static String findItemsByConsignmentIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.status, ");
        query.append("     barcode_number, ");
        query.append("     weight, ");
        query.append("     height, ");
        query.append("     width, ");
        query.append("     length, ");
        query.append("     i.current_location_code ");
        query.append(" FROM \"Items\" i ");
        query.append(" INNER JOIN \"Consignments\" c ");
        query.append("     ON i.cons_id = c.id ");
        query.append(" WHERE c.consignment_id = ? ");

        return query.toString();
    }

    public static String findByBarcodeNumberQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.status, ");
        query.append("     i.barcode_number, ");
        query.append("     i.current_location_code, ");
        query.append("     c.id  AS consignment_pk, ");
        query.append("     c.consignment_id ");
        query.append(" FROM \"Items\" i ");
        query.append(" INNER JOIN \"Consignments\" c ");
        query.append("     ON i.cons_id = c.id ");
        query.append(" WHERE i.barcode_number = ? ");

        return query.toString();
    }

    public static String findByConsIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.status, ");
        query.append("     i.current_location_code, ");
        query.append("     i.barcode_number, ");
        query.append("     i.weight, ");
        query.append("     i.height, ");
        query.append("     i.width, ");
        query.append("     i.length ");
        query.append(" FROM \"Items\" i ");
        query.append(" WHERE i.cons_id = ? ");

        return query.toString();
    }

    public static String findLastBarcodeNumberQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" SELECT ");
        query.append("     i.barcode_number ");
        query.append(" FROM \"Items\" i ");
        query.append(" ORDER BY id DESC ");
        query.append(" LIMIT 1 ");
        return query.toString();
    }

    public static String findByIdQuery() {
        StringBuilder query = new StringBuilder();

        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.status, ");
        query.append("     i.current_location_code, ");
        query.append("     c.id  AS consignment_pk, ");
        query.append("     c.consignment_id ");
        query.append(" FROM \"Items\" i ");
        query.append(" INNER JOIN \"Consignments\" c ");
        query.append("     ON i.cons_id = c.id ");
        query.append(" WHERE i.id = ? ");
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

    public static String findAllByStatusQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        query.append("     i.id, ");
        query.append("     i.item_id, ");
        query.append("     i.status, ");
        query.append("     i.weight, ");
        query.append("     i.current_location_code, ");
        query.append("     dc.name         AS dest_name, ");
        query.append("     dc.address_line1 AS dest_address_line1, ");
        query.append("     dc.address_line2 AS dest_address_line2, ");
        query.append("     dc.state         AS dest_state, ");
        query.append("     dc.suburb        AS dest_suburb, ");
        query.append("     dc.postcode      AS dest_postcode, ");
        query.append("     dc.country       AS dest_country ");
        query.append(" FROM \"Items\" i ");
        query.append(" JOIN \"Consignments\" c ON i.cons_id = c.id ");
        query.append(" LEFT JOIN \"Contacts\" dc ON c.destination_contact_id = dc.id ");
        query.append(" WHERE i.status = ? ");
        query.append(" ORDER BY i.id ASC ");

        return query.toString();
    }
}