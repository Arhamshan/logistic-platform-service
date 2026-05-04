package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.platform.repository.ConsignmentRepository;
import com.logistic.platform.util.ConsignmentQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Optional;

@Repository("consignmentReaderRepository")
public class ConsignmentReaderRepository implements ConsignmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ConsignmentReaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Consignment> findByConsignmentId(String consignmentId, String requestId) {

        LOGGER.info("START [DB-READ] findConsignmentById={}", consignmentId);

        try {
            String sql = ConsignmentQueryUtil.findConsignmentByIdQuery();

            List<Consignment> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Consignment consignment = new Consignment();

                consignment.setId(rs.getLong("id"));
                consignment.setConsignmentId(rs.getString("consignment_id"));
                consignment.setStatus(ConsignmentStatus.valueOf(rs.getString("status")));
                consignment.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                consignment.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());

                return consignment;
            }, consignmentId);

            return result.stream().findFirst();

        } catch (Exception e) {
            LOGGER.error("ERROR fetching consignment", e);
            throw new RuntimeException("Failed to fetch consignment", e);
        }
    }
}