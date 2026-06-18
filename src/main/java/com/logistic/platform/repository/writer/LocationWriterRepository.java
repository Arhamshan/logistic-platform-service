package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Location;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.exception.LocationCreateException;
import com.logistic.platform.repository.LocationRepository;
import com.logistic.platform.util.LocationQueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Repository
public class LocationWriterRepository implements LocationRepository {

    private static final Logger LOGGER = LogManager.getLogger(LocationWriterRepository.class);

    @Qualifier("writer")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long save(Location location, String requestId) throws LocationCreateException {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] save: location={}", CommonUtils.convertToString(location), requestId);

        KeyHolder key = new GeneratedKeyHolder();
        Long id = null;

        try {
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    final PreparedStatement ps = con.prepareStatement(LocationQueryUtil.insertQuery(),
                            Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, location.getLocationCode());
                    ps.setString(2, location.getName());
                    ps.setString(3, location.getType() != null ? location.getType().name() : null);
                    ps.setString(4, location.getCity());
                    ps.setString(5, location.getCountry());
                    ps.setString(6, location.getLatitude());
                    ps.setString(7, location.getLongitude());
                    ps.setObject(8, LocalDateTime.now());
                    ps.setString(9, location.getCreatedBy());
                    ps.setObject(10, LocalDateTime.now());
                    ps.setString(11, location.getUpdatedBy());
                    return ps;
                }
            }, key);

            if (Objects.requireNonNull(key.getKeys()).size() > 1) {
                id =  (long) Objects.requireNonNull(key.getKeys()).get("id");
            } else {
                id= Objects.requireNonNull(key.getKey()).longValue();
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] save: Ex={}|Trace={}", requestId,
                    e.getMessage(), e.getStackTrace());

            throw new LocationCreateException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create location", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] save: location id={}|timeTaken={}", requestId,
                    id, CommonUtils.getExecutionTime(startTime));
        }

        return id;
    }

    public Boolean update(Location location, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] updateLocation", requestId);

        int rows = 0;

        try {

            String sql = LocationQueryUtil.updateLocationQuery();

            rows = jdbcTemplate.update(sql,
                    location.getName(),
                    location.getType().name(),
                    location.getCountry(),
                    location.getCity(),
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getUpdatedBy(),
                    location.getId()
            );

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] updateLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] updateLocation: rows={}|timeTaken={}",
                    requestId, rows, CommonUtils.getExecutionTime(startTime));
        }

        return rows > 0;
    }

    public Boolean deleteById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] deleteById: id={}",
                requestId, id);

        int rows = 0;

        try {
            String sql = LocationQueryUtil.deleteByIdQuery();
            rows = jdbcTemplate.update(sql, id);

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] deleteById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to delete location", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] deleteById: rows={}|timeTaken={}",
                    requestId, rows, CommonUtils.getExecutionTime(startTime));
        }

        return rows > 0;
    }

}
