package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.EventRepository;
import com.logistic.platform.util.EventQueryUtil;
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
public class EventReaderRepository implements EventRepository {

    private static final Logger LOGGER = LogManager.getLogger(EventReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Event> findByConsignmentId(String consignmentId, String requestId) {

        LOGGER.info("START [DB-READ] [RequestId={}] findByConsignmentId={}", requestId, consignmentId);

        try {
            return List.of();

        } catch (Exception e) {
            LOGGER.error("ERROR [DB-READ] findByConsignmentId failed", e);
            throw new RuntimeException("Failed to fetch events", e);
        }
    }

    @Override
    public List<Event> findByItemId(String itemId, String requestId) {
        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByItemId: itemId={}", requestId, itemId);

        List<Event> events = null;

        try {
            String sql = EventQueryUtil.findEventsByItemIdQuery();

            Item item = new Item();


            events = jdbcTemplate.query(sql, new Object[]{itemId}, new RowMapper<Event>() {
                @Override
                public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Event event = new Event();
                    event.setId(rs.getLong("id"));
                    event.getItem().getConsignment().setConsignmentId(rs.getString("cons_item_id"));
                    event.setEventType(EventType.valueOf(rs.getString("event_type")));
                    event.setEventLocationCode(rs.getString("event_location_code"));
                    event.setDescription(rs.getString("description"));
                    event.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    event.setCreatedBy(rs.getString("created_by"));
                    event.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                    event.setUpdatedBy(rs.getString("updated_by"));
                    return event;
                }
            });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByItemId: size={}|timeTaken={}",
                    requestId, events != null ? events.size() : 0, CommonUtils.getExecutionTime(startTime));
        }

        return events;
    }

}