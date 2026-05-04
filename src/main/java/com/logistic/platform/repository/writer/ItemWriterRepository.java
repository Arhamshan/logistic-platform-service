package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Item;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ItemRepository;
import com.logistic.platform.util.ItemQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class ItemWriterRepository implements ItemRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemWriterRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ItemWriterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Item item, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] save: item={}", requestId, CommonUtils.convertToString(item));

        Long consItemId = null;

        try {
            String sql = ItemQueryUtil.insertItemQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, item.getItemId());
                ps.setLong(2, item.getConsignment().getId());
                ps.setString(3, String.valueOf(item.getStatus()));
                ps.setString(4, item.getCurrentLocationCode());
                ps.setTimestamp(5, Timestamp.valueOf(item.getCreatedDate() != null ?
                        item.getCreatedDate() : LocalDateTime.now()));
                ps.setString(6, item.getCreatedBy());
                ps.setTimestamp(7, Timestamp.valueOf(item.getUpdatedDate() != null ?
                        item.getUpdatedDate() : LocalDateTime.now()));
                ps.setString(8, item.getUpdatedBy());

                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            consItemId = ((Long) keys.get("id"));

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save item", e);
        }

        LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] save: itemId={}|timeTaken={}",
                requestId, consItemId, CommonUtils.getExecutionTime(startTime));

        return consItemId;
    }

    public void updateItemStatusAndLocation(Item item, String requestId) {

        LOGGER.info("START [DB-WRITE] updateItemStatus itemId={}", item.getItemId());

        try {
            String sql = ItemQueryUtil.updateItemStatusAndLocationQuery();

            jdbcTemplate.update(sql,
                    item.getStatus().name(),
                    item.getCurrentLocationCode(),
                    LocalDateTime.now(),
                    "SYSTEM",
                    item.getItemId()
            );

        } catch (Exception e) {
            LOGGER.error("ERROR updating item", e);
            throw new RuntimeException("Failed to update item", e);
        }
    }
}