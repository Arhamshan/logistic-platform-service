package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.util.PodQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PodReaderRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PodReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PodReaderRepository(
            @Qualifier("reader") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Pod findByConsItemId(Long consItemId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByConsItemId: consItemId={}",
                requestId, consItemId);

        Pod result = null;

        try {
            String sql = PodQueryUtil.findByConsItemIdQuery();

            List<Pod> rows = jdbcTemplate.query(
                    sql,
                    new Object[]{consItemId},
                    (rs, rowNum) -> {
                        Pod pod = new Pod();
                        pod.setId(rs.getLong("id"));
                        pod.setReceivedBy(rs.getString("received_by"));
                        pod.setReceiverContact(rs.getString("receiver_contact"));
                        pod.setPodPath(rs.getString("pod_path"));
                        pod.setDeliveredBy(rs.getString("delivered_by"));
                        return pod;
                    });

            result = rows.isEmpty() ? null : rows.get(0);

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByConsItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch pod", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByConsItemId: found={}|timeTaken={}",
                    requestId, result != null, CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}