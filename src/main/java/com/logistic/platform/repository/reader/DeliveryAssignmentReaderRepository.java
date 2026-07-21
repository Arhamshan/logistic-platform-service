package com.logistic.platform.repository.reader;

import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;
import com.logistic.platform.repository.DeliveryAssignmentRepository;
import com.logistic.platform.util.DeliveryAssignmentQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeliveryAssignmentReaderRepository implements DeliveryAssignmentRepository {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DeliveryAssignmentReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public DeliveryAssignmentReaderRepository(
            @Qualifier("reader") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DeliveryAssignmentSummaryDto findSummary(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findSummary",
                requestId);

        DeliveryAssignmentSummaryDto result = null;

        try {
            String sql = DeliveryAssignmentQueryUtil.findSummaryQuery();

            List<DeliveryAssignmentSummaryDto> rows = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new DeliveryAssignmentSummaryDto(
                            rs.getLong("total_intransit_items"),
                            rs.getLong("today_assignments"),
                            rs.getLong("total_drivers")
                    ));

            result = rows.isEmpty() ? null : rows.get(0);

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch delivery assignment summary", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findSummary: result={}|timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(result),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}