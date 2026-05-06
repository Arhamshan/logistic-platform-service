package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
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

    public Optional<Item> findByConsignmentIdAndItemId(String itemId,
                                       String consignmentId,
                                       String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByItemId: itemId={}|consignmentId={}",
                requestId, itemId, consignmentId);

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
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("DB failure", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByItemId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }
}