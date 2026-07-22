package com.logistic.platform.repository.reader;

import com.logistic.common.util.CommonUtils;
import com.logistic.platform.util.DeliveryAssignmentQueryUtil;
import com.logistic.platform.vo.DeliveryAssignmentSummaryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeliveryAssignmentReaderRepository {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DeliveryAssignmentReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public DeliveryAssignmentReaderRepository(
            @Qualifier("reader") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DeliveryAssignmentSummaryVo findSummary(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findSummary", requestId);

        DeliveryAssignmentSummaryVo result = null;

        try {
            String sql = DeliveryAssignmentQueryUtil.findSummaryQuery();

            List<DeliveryAssignmentSummaryVo> rows = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new DeliveryAssignmentSummaryVo(
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