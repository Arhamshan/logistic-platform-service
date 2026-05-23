package com.logistic.platform.repository.reader;

import com.logistic.common.entity.User;
import com.logistic.common.enums.Role;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.UserRepository;
import com.logistic.platform.util.UserQueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserReaderRepository implements UserRepository {

    private static final Logger LOGGER = LogManager.getLogger(UserReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByUsername(String username, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByUsername: username={}",
                requestId, username);

        User user = null;

        try {

            List<User> users = this.jdbcTemplate.query(UserQueryUtil.findUserByUsernameQuery(), new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User usr = new User();
                    usr.setUsername(rs.getString("username"));
                    usr.setPassword(rs.getString("password"));
                    usr.setRole(Role.from(rs.getString("role")));

                    return usr;
                }
            }, username);

            if (users != null && !users.isEmpty()) {
                user = users.get(0);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByUsername: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to find user. ", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByUsername: user={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(user), CommonUtils.getExecutionTime(startTime));
        }

        return user;
    }

}