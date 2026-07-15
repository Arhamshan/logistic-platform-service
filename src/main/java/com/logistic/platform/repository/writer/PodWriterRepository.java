package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.PodRepository;
import com.logistic.platform.util.PodQueryUtil;
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
public class PodWriterRepository implements PodRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PodWriterRepository.class);

    @Qualifier("writer")
    private final JdbcTemplate jdbcTemplate;

    public PodWriterRepository(
            @Qualifier("writer") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Pod pod, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] save: pod={}",
                requestId, CommonUtils.convertToString(pod));

        Long generatedId = null;

        try {
            String sql = PodQueryUtil.insertPodQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, pod.getItem().getId());
                ps.setString(2, pod.getReceivedBy());
                ps.setString(3, pod.getReceiverContact());
                ps.setString(4, pod.getRemarks());
                ps.setString(5, pod.getPodPath());
                ps.setTimestamp(6, pod.getDeliveredAt() != null
                        ? Timestamp.valueOf(pod.getDeliveredAt()) : null);
                ps.setString(7, pod.getDeliveredBy());
                ps.setTimestamp(8, Timestamp.valueOf(pod.getCreatedDate()));
                ps.setString(9, pod.getCreatedBy());
                ps.setTimestamp(10, Timestamp.valueOf(pod.getUpdatedDate()));
                ps.setString(11, pod.getUpdatedBy());

                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null) {
                generatedId = ((Number) keyHolder.getKeys().get("id")).longValue();
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save POD", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] save: generatedId={}|timeTaken={}",
                    requestId, generatedId, CommonUtils.getExecutionTime(startTime));
        }

        return generatedId;
    }
}