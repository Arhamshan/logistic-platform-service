package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Contact;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ItemRepository;
import com.logistic.platform.util.ItemQueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemReaderRepository implements ItemRepository {

    private static final Logger LOGGER = LogManager.getLogger(ItemReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Item> findByConsignmentIdAndItemId(String consignmentId,
                                       String itemId,
                                       String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByConsignmentIdAndItemId: consignmentId={}|itemId={}",
                requestId, consignmentId, itemId);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findByItemIdAndConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{itemId, consignmentId},
                    new RowMapper<Item>() {
                        @Override
                        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {

                            // 1. Map consignment fully first
                            Consignment consignment = new Consignment();
                            consignment.setId(rs.getLong("consignment_pk"));
                            consignment.setConsignmentId(rs.getString("consignment_id"));

                            // 2. Map item and attach consignment
                            Item item = new Item();
                            item.setId(rs.getLong("id"));
                            item.setItemId(rs.getString("item_id"));
                            item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                            item.setCurrentLocationCode(rs.getString("current_location_code"));
                            item.setConsignment(consignment);

                            return item;
                        }
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByConsignmentIdAndItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("DB failure", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByConsignmentIdAndItemId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }

    @Override
    public List<Item> findItemsByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findItemsByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findItemsByConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{consignmentId},
                    (rs, rowNum) -> {
                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setBarcodeNumber(rs.getString("barcode_number"));
                        item.setWeight(Math.round(rs.getFloat("weight") * 100) / 100f);
                        item.setHeight(Math.round(rs.getFloat("height") * 100) / 100f);
                        item.setWidth(Math.round(rs.getFloat("width") * 100) / 100f);
                        item.setLength(Math.round(rs.getFloat("length") * 100) / 100f);
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findItemsByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch items for consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findItemsByConsignmentId: result={}|timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(result),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    @Override
    public String findLastBarcodeNumber(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findLastBarcodeNumber", requestId);

        List<String> result = null;
        String lastBarcode = null;

        try {
            String sql = ItemQueryUtil.findLastBarcodeNumberQuery();

            result = jdbcTemplate.query(sql, (rs, rowNum) ->
                    rs.getString("barcode_number"));

            if (result != null && !result.isEmpty()) {
                lastBarcode = result.get(0);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findLastBarcodeNumber: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findLastBarcodeNumber: lastBarcode={}|timeTaken={}",
                    requestId, lastBarcode, CommonUtils.getExecutionTime(startTime));
        }

        return lastBarcode;
    }

    @Override
    public Optional<Item> findByBarcodeNumber(String barcodeNumber, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByBarcodeNumber: barcodeNumber={}",
                requestId, barcodeNumber);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findByBarcodeNumberQuery();

            result = jdbcTemplate.query(sql, new Object[]{barcodeNumber},
                    (rs, rowNum) -> {
                        Consignment consignment = new Consignment();
                        consignment.setId(rs.getLong("consignment_pk"));
                        consignment.setConsignmentId(rs.getString("consignment_id"));

                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setBarcodeNumber(rs.getString("barcode_number"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        item.setConsignment(consignment);
                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByBarcodeNumber: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch item by barcode", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByBarcodeNumber: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }

    public Optional<Item> findById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findById: id={}",
                requestId, id);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findByIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{id},
                    (rs, rowNum) -> {
                        Consignment consignment = new Consignment();
                        consignment.setId(rs.getLong("consignment_pk"));
                        consignment.setConsignmentId(rs.getString("consignment_id"));

                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        item.setConsignment(consignment);
                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch item by id", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findById: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return (result != null && !result.isEmpty())
                ? Optional.of(result.get(0))
                : Optional.empty();
    }

    @Override
    public List<Item> findByConsId(Long consId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByConsId: consId={}", requestId, consId);

        List<Item> itemsList = null;

        try {
            String sql = ItemQueryUtil.findByConsIdQuery();

            itemsList = jdbcTemplate.query(sql, new Object[]{consId},
                    (rs, rowNum) -> {
                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        item.setBarcodeNumber(rs.getString("barcode_number"));
                        item.setWeight(rs.getFloat("weight"));
                        item.setHeight(rs.getFloat("height"));
                        item.setWidth(rs.getFloat("width"));
                        item.setLength(rs.getFloat("length"));

                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByConsId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch item by consId", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByConsId: itemsList={}|timeTaken={}",
                    requestId, itemsList, CommonUtils.getExecutionTime(startTime));
        }

        return itemsList;
    }

    public List<Item> findAllByStatus(String status, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findAllByStatus: status={}",
                requestId, status);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findAllByStatusQuery();

            result = jdbcTemplate.query(sql, new Object[]{status},
                    (rs, rowNum) -> {

                        // ── Destination contact ──
                        Contact destContact = new Contact();
                        destContact.setName(rs.getString("dest_name"));
                        destContact.setAddressLine1(rs.getString("dest_address_line1"));
                        destContact.setAddressLine2(rs.getString("dest_address_line2"));
                        destContact.setState(rs.getString("dest_state"));
                        destContact.setSuburb(rs.getString("dest_suburb"));
                        destContact.setPostcode(rs.getString("dest_postcode"));
                        destContact.setCountry(rs.getString("dest_country"));

                        // ── Consignment with destination contact ──
                        Consignment consignment = new Consignment();
                        consignment.setDestinationContact(destContact);

                        // ── Item ──
                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setWeight(rs.getFloat("weight"));
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        item.setConsignment(consignment);

                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findAllByStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch items by status", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findAllByStatus: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    public List<Item> findAllByDriverId(Long driverId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findAllByDriverId: driverId={}",
                requestId, driverId);

        List<Item> result = null;

        try {
            String sql = ItemQueryUtil.findAllByDriverIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{driverId},
                    (rs, rowNum) -> {

                        // ── Destination contact ──
                        Contact destContact = new Contact();
                        destContact.setName(rs.getString("dest_name"));
                        destContact.setAddressLine1(rs.getString("dest_address_line1"));
                        destContact.setAddressLine2(rs.getString("dest_address_line2"));
                        destContact.setState(rs.getString("dest_state"));
                        destContact.setSuburb(rs.getString("dest_suburb"));
                        destContact.setPostcode(rs.getString("dest_postcode"));
                        destContact.setCountry(rs.getString("dest_country"));

                        // ── Consignment with destination contact ──
                        Consignment consignment = new Consignment();
                        consignment.setConsignmentId(rs.getString("consignment_id"));
                        consignment.setDestinationContact(destContact);

                        // ── Item ──
                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setItemId(rs.getString("item_id"));
                        item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                        item.setWeight(rs.getFloat("weight"));
                        item.setCurrentLocationCode(rs.getString("current_location_code"));
                        item.setConsignment(consignment);

                        return item;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findAllByDriverId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch items by driverId", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findAllByDriverId: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}