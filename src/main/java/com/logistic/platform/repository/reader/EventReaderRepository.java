package com.logistic.platform.repository.reader;

import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.EventRepository;
import com.logistic.platform.util.EventQueryUtil;
import com.logistic.platform.vo.TrackingEventVo;
import com.logistic.platform.vo.TrackingLocationVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventReaderRepository implements EventRepository {

    private static final Logger LOGGER = LogManager.getLogger(EventReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TrackingEventVo> findTrackingEventsByItemId(Long itemId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findTrackingEventsByItemId: itemId={}",
                requestId, itemId);

        List<TrackingEventVo> result = null;

        try {
            String sql = EventQueryUtil.findTrackingEventsByItemIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{itemId},
                    (rs, rowNum) -> {
                        TrackingEventVo event = new TrackingEventVo();
                        event.setEventType(rs.getString("event_type"));
                        event.setDescription(rs.getString("description"));
                        event.setEventTime(rs.getString("created_date"));

                        TrackingLocationVo location = new TrackingLocationVo(
                                rs.getString("location_name"),
                                rs.getString("location_type"),
                                rs.getString("location_code")
                        );
                        event.setLocation(location);

                        return event;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findTrackingEventsByItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch tracking events", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findTrackingEventsByItemId: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

}