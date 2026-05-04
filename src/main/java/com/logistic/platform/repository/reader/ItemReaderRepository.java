package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.platform.repository.ItemRepository;
import com.logistic.platform.util.ItemQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemReaderRepository implements ItemRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ItemReaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Item> findByConsignmentId(String consignmentId, String requestId) {

        LOGGER.info("START [DB-READ] findItemsByConsignmentId={}", consignmentId);

        try {
            String sql = ItemQueryUtil.findItemsByConsignmentIdQuery();

            Consignment consignment = new Consignment();
            consignment.setConsignmentId(consignmentId);

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Item item = new Item();
                item.setId(rs.getLong("id"));
                item.setItemId(rs.getString("item_id"));
                item.setConsignment(consignment);
                consignment.setConsignmentId(rs.getString("consignment_id"));
                item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                item.setCurrentLocationCode(rs.getString("current_location_code"));
                return item;
            }, consignmentId);

        } catch (Exception e) {
            LOGGER.error("ERROR fetching items by consignmentId", e);
            throw new RuntimeException("Failed to fetch items", e);
        }
    }

    public Optional<Item> findByItemId(String itemId,
                                       String consignmentId,
                                       String requestId) {

        LOGGER.info("START [DB-READ] findItemById={}", itemId);

        try {
            String sql = ItemQueryUtil.findItemByIdQuery();

            List<Item> result = jdbcTemplate.query(sql, (rs, rowNum) -> {

                Item item = new Item();
                item.setId(rs.getLong("id"));
                item.setItemId(rs.getString("item_id"));
                item.setStatus(ItemStatus.valueOf(rs.getString("status")));
                item.setCurrentLocationCode(rs.getString("current_location_code"));

                String consId = rs.getString("consignment_id");

                if (consId != null) {
                    Consignment consignment = new Consignment();
                    consignment.setConsignmentId(consId);
                    item.setConsignment(consignment);
                }

                return item;

            }, itemId, consignmentId);

            return result.stream().findFirst();

        } catch (Exception e) {
            LOGGER.error("DB ERROR finding item", e);
            throw new RuntimeException("DB failure", e);
        }
    }
}