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

}
