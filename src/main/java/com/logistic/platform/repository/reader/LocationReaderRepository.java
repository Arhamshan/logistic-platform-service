package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Location;
import com.logistic.common.enums.LocationType;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.LocationRepository;
import com.logistic.platform.util.LocationQueryUtil;
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
public class LocationReaderRepository implements LocationRepository {

    private static final Logger LOGGER = LogManager.getLogger(LocationReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String findLastLocationCode(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findLastLocationCode: ", requestId);

        List<Location> locations = null;
        String lastLocationCode = null;

        try {
            locations = this.jdbcTemplate.query(LocationQueryUtil.findLastLocationCodeQuery(), new RowMapper<Location>() {

                @Override
                public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Location location = new Location();
                    location.setLocationCode(rs.getString("location_code"));

                    return location;
                }
            });

            if (locations != null && !locations.isEmpty()) {
                lastLocationCode = locations.get(0).getLocationCode();
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findLastLocationCode: Ex={}|Trace={}", requestId,
                    e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findLastLocationCode: lastLocationCode={}|timeTaken={}", requestId,
                    lastLocationCode, CommonUtils.getExecutionTime(startTime));
        }

        return lastLocationCode;
    }

    @Override
    public List<Location> findAllLocations(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findAllLocations: ", requestId);

        List<Location> locations = null;

        try {

            locations = this.jdbcTemplate.query(
                    LocationQueryUtil.findAllLocationsQuery(),
                    new RowMapper<Location>() {
                        @Override
                        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Location location = new Location();
                            location.setId(rs.getLong("id"));
                            location.setName(rs.getString("name"));
                            location.setLocationCode(rs.getString("location_code"));
                            location.setCountry(rs.getString("country"));
                            location.setCity(rs.getString("city"));
                            location.setType(LocationType.valueOf(rs.getString("type")));
                            location.setLatitude(rs.getString("latitude"));
                            location.setLongitude(rs.getString("longitude"));
                            return location;
                        }
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findAllLocations: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findAllLocations: size={}|timeTaken={}",
                    requestId,
                    locations != null ? locations.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return locations;
    }
}
