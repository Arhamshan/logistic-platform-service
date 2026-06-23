package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Item;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ConsignmentRepository;
import com.logistic.platform.util.ConsignmentQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class ConsignmentWriterRepository implements ConsignmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentWriterRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ConsignmentWriterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] save: consignment={}", requestId, CommonUtils.convertToString(consignment));

        Long consId = null;

        try {
            String sql = ConsignmentQueryUtil.insertQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, consignment.getConsignmentId());
                ps.setLong(2, consignment.getSenderContact().getId());
                ps.setLong(3, consignment.getDestinationContact().getId());
                ps.setString(4, consignment.getStatus().name());
                ps.setTimestamp(5, Timestamp.valueOf(consignment.getCreatedDate() != null ?
                        consignment.getCreatedDate() : LocalDateTime.now()));
                ps.setString(6, consignment.getCreatedBy());
                ps.setTimestamp(7, Timestamp.valueOf(consignment.getUpdatedDate() != null ?
                        consignment.getUpdatedDate() : LocalDateTime.now()));
                ps.setString(8, consignment.getUpdatedBy());

                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            consId = ((Long) keys.get("id"));

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save consignment", e);
        }

        LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] save: consId={}|timeTaken={}",
                requestId, consId, CommonUtils.getExecutionTime(startTime));

        return consId;
    }

    public Boolean updateStatus(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        // print full object
        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] updateStatus: consignment={}",
                requestId, CommonUtils.convertToString(consignment));

        Boolean isUpdated = false;

        try {
            String sql = ConsignmentQueryUtil.updateConsignmentStatusQuery();

            int rowsAffected = jdbcTemplate.update(sql,
                    consignment.getStatus().name(),
                    LocalDateTime.now(),
                    consignment.getUpdatedBy(),
                    consignment.getId()
            );
            isUpdated = rowsAffected >0;

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw new RuntimeException("Failed to update consignment", e);
        } finally {

            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateStatus: id={}|timeTaken={}",
                    requestId, consignment.getId(), CommonUtils.getExecutionTime(startTime));
        }

        return isUpdated;
    }

    public Boolean deleteById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] deleteById: id={}",
                requestId, id);

        Boolean isDeleted = Boolean.FALSE;

        try {
            String sql = ConsignmentQueryUtil.deleteByIdQuery();

            int rowsAffected = jdbcTemplate.update(sql, id);
            isDeleted = rowsAffected > 0;

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] deleteById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to delete consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] deleteById: isDeleted={}|timeTaken={}",
                    requestId, isDeleted, CommonUtils.getExecutionTime(startTime));
        }

        return isDeleted;
    }

    public Boolean updateConsignment(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] updateConsignment: consignment={}",
                requestId, CommonUtils.convertToString(consignment));

        Boolean isUpdated = Boolean.FALSE;

        try {
            String sql = ConsignmentQueryUtil.updateConsignmentQuery();

            int rowsAffected = jdbcTemplate.update(sql,
                    consignment.getConsignmentId(),
                    LocalDateTime.now(),
                    consignment.getUpdatedBy(),
                    consignment.getId()
            );

            isUpdated = rowsAffected > 0;

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateConsignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to update consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateConsignment: isUpdated={}|timeTaken={}",
                    requestId, isUpdated, CommonUtils.getExecutionTime(startTime));
        }

        return isUpdated;
    }

    @Override
    public Boolean updateItems(List<Item> items, String username, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] updateItems: itemCount={}",
                requestId, items.size());

        Boolean isUpdated = Boolean.FALSE;

        try {
            String sql = ConsignmentQueryUtil.updateItemQuery();
            LocalDateTime now = LocalDateTime.now();

            int[] rowsAffected = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Item item = items.get(i);
                    ps.setObject(1, item.getItemId());
                    ps.setObject(2, item.getWeight());
                    ps.setObject(3, item.getHeight());
                    ps.setObject(4, item.getLength());
                    ps.setObject(5, item.getWidth());
                    ps.setObject(6, item.getCurrentLocationCode());
                    ps.setObject(7, now);
                    ps.setString(8, username);
                    ps.setObject(9, item.getId());
                }

                @Override
                public int getBatchSize() {
                    return items.size();
                }
            });

            // Consider updated if at least one row was affected
            isUpdated = Arrays.stream(rowsAffected).anyMatch(r -> r > 0);

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateItems: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to update items", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateItems: isUpdated={}|timeTaken={}",
                    requestId, isUpdated, CommonUtils.getExecutionTime(startTime));
        }

        return isUpdated;
    }
}