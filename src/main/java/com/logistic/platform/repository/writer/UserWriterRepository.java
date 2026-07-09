package com.logistic.platform.repository.writer;

import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.UserRepository;
import com.logistic.platform.util.UserQueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class UserWriterRepository implements UserRepository {

    private static final Logger LOGGER = LogManager.getLogger(UserWriterRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean save(User user, String requestId) {

        long startTime = System.currentTimeMillis();
        int rows = 0;

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] createUser", requestId);

        try {

            String sql = UserQueryUtil.insertUserQuery();

            // Convert LocalDateTime to Timestamp for JDBC
            Timestamp createdTimestamp = user.getCreatedDate() != null ?
                    Timestamp.valueOf(user.getCreatedDate()) : Timestamp.valueOf(LocalDateTime.now());
            Timestamp updatedTimestamp = user.getUpdatedDate() != null ?
                    Timestamp.valueOf(user.getUpdatedDate()) : Timestamp.valueOf(LocalDateTime.now());

            Long contactId = (user.getContact() != null)
                    ? user.getContact().getId()
                    : null;

            rows = jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole().name(),
                    user.getStatus().name(),
                    contactId,
                    createdTimestamp,
                    user.getCreatedBy(),
                    updatedTimestamp,
                    user.getUpdatedBy()
            );

        } catch (Exception e) {

            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] createUser: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] createUser: rows={} | timeTaken={}",
                    requestId, rows, CommonUtils.getExecutionTime(startTime));
        }

        return rows > 0;
    }
}