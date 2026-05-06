package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Consignment;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ConsignmentRepository;
import com.logistic.platform.util.ConsignmentQueryUtil;
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

        try {
            String sql = ConsignmentQueryUtil.updateConsignmentStatusQuery();

            jdbcTemplate.update(sql,
                    consignment.getStatus().name(),
                    LocalDateTime.now(),
                    consignment.getUpdatedBy(),
                    consignment.getId()
            );

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw new RuntimeException("Failed to update consignment", e);
        } finally {

            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateStatus: id={}|timeTaken={}",
                    requestId, consignment.getId(), CommonUtils.getExecutionTime(startTime));
        }

        return false;
    }
}