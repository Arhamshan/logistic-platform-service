package com.logistic.platform.repository.writer;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.EventType;
import com.logistic.platform.repository.EventRepository;
import com.logistic.platform.util.EventQueryUtil;
import com.logistic.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class EventWriterRepository implements EventRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventWriterRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to convert ResultSet to Event object
    private final RowMapper<Event> eventRowMapper = (rs, rowNum) -> {
        Event event = new Event();
        event.setId(rs.getLong("id"));

        // Set Item object with ID (you can fetch full item details if needed)
        Item item = new Item();
        item.setId(rs.getLong("item_id"));
        event.setItem(item);

        String eventTypeStr = rs.getString("event_type");
        if (eventTypeStr != null) {
            event.setEventType(EventType.valueOf(eventTypeStr));
        }

        event.setEventLocationCode(rs.getString("event_location_code"));
        event.setDescription(rs.getString("description"));

        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            event.setCreatedDate(createdDate.toLocalDateTime());
        }

        event.setCreatedBy(rs.getString("created_by"));

        Timestamp updatedDate = rs.getTimestamp("updated_date");
        if (updatedDate != null) {
            event.setUpdatedDate(updatedDate.toLocalDateTime());
        }

        event.setUpdatedBy(rs.getString("updated_by"));

        return event;
    };

    @Override
    public Event save(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] saveEvent: event={}", requestId, CommonUtils.convertToString(event));

        try {
            String sql = EventQueryUtil.insertEventQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, event.getItem().getId());
                ps.setString(2, event.getEventType().name());
                ps.setString(3, event.getEventLocationCode());
                ps.setString(4, event.getDescription());
                ps.setTimestamp(5, Timestamp.valueOf(event.getCreatedDate() != null ?
                        event.getCreatedDate() : LocalDateTime.now()));
                ps.setString(6, event.getCreatedBy());
                ps.setTimestamp(7, Timestamp.valueOf(event.getUpdatedDate() != null ?
                        event.getUpdatedDate() : LocalDateTime.now()));
                ps.setString(8, event.getUpdatedBy());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            event.setId((Long) keys.get("id"));

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] saveEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save event", e);
        }

        LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] saveEvent: eventId={}|   timeTaken={}",
                requestId, event.getId(), CommonUtils.getExecutionTime(startTime));

        return event;
    }

}