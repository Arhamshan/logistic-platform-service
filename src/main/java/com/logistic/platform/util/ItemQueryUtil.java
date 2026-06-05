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
        query.append("     i.current_location_code ");
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
}