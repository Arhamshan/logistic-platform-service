package com.logistic.platform.repository.writer;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.DeliveryAssignmentRepository;
import com.logistic.platform.util.DeliveryAssignmentQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@Repository
public class DeliveryAssignmentWriterRepository implements DeliveryAssignmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryAssignmentWriterRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public DeliveryAssignmentWriterRepository(
            @Qualifier("writer") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(DeliveryAssignment assignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] save: assignment={}",
                requestId, CommonUtils.convertToString(assignment));

        Long generatedId = null;

        try {
            String sql = DeliveryAssignmentQueryUtil.insertDeliveryAssignmentQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, assignment.getItem().getId());
                ps.setLong(2, assignment.getDriver().getId());
                ps.setString(3, assignment.getAssignedBy());
                ps.setTimestamp(4, assignment.getAssignedDatetime() != null
                        ? Timestamp.valueOf(assignment.getAssignedDatetime()) : null);
                ps.setString(5, assignment.getStatus());
                ps.setString(6, assignment.getRemarks());
                ps.setTimestamp(7, assignment.getCreatedDate() != null
                        ? Timestamp.valueOf(assignment.getCreatedDate()) : null);
                ps.setTimestamp(8, assignment.getUpdatedDate() != null
                        ? Timestamp.valueOf(assignment.getUpdatedDate()) : null);
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null) {
                generatedId = ((Number) keyHolder.getKeys().get("id")).longValue();
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save DeliveryAssignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] save: generatedId={}|timeTaken={}",
                    requestId, generatedId, CommonUtils.getExecutionTime(startTime));
        }

        return generatedId;
    }

    public Boolean updateStatus(Long driverId, Long consItemId, String status, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] updateStatus: driverId={}|consItemId={}|status={}",
                requestId, driverId, consItemId, status);

        int rows = 0;

        try {
            String sql = DeliveryAssignmentQueryUtil.updateStatusByDriverIdAndConsItemIdQuery();

            rows = jdbcTemplate.update(sql, status, driverId, consItemId);

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to update delivery assignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateStatus: rows={}|timeTaken={}",
                    requestId, rows, CommonUtils.getExecutionTime(startTime));
        }

        return rows > 0;
    }
}