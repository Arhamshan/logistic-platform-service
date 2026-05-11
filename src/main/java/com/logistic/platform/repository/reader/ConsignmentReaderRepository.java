package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ConsignmentRepository;
import com.logistic.platform.util.ConsignmentQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("consignmentReaderRepository")
public class ConsignmentReaderRepository implements ConsignmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ConsignmentReaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Consignment> findByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        List<Consignment> result = null;

        try {
            String sql = ConsignmentQueryUtil.findByConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{consignmentId},
                    new RowMapper<Consignment>() {
                        @Override
                        public Consignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Consignment consignment = new Consignment();
                            consignment.setId(rs.getLong("id"));
                            consignment.setConsignmentId(rs.getString("consignment_id"));
                            return consignment;
                        }
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw new RuntimeException("Failed to fetch consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }

    public Optional<Consignment> findTrackingByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        List<Consignment> result = null;

        try {
            String sql = ConsignmentQueryUtil.findTrackingByConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{consignmentId},
                    (rs, rowNum) -> {
                        Consignment consignment = new Consignment();
                        consignment.setConsignmentId(rs.getString("consignment_id"));
                        consignment.setStatus(ConsignmentStatus.valueOf(rs.getString("status")));
                        return consignment;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch consignment tracking", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }
}
