package com.logistic.platform.repository;

import com.logistic.common.entity.Contact;
import com.logistic.platform.repository.ContactRepository;
import com.logistic.platform.util.ContactQueryUtil;
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
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContactRepositoryImpl implements ContactRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to convert ResultSet to Contact object
    private final RowMapper<Contact> contactRowMapper = (rs, rowNum) -> {
        Contact contact = new Contact();
        contact.setId(rs.getLong("id"));
        contact.setName(rs.getString("name"));
        contact.setEmail(rs.getString("email"));
        contact.setPhone(rs.getString("phone"));
        contact.setAddressLine1(rs.getString("address_line1"));
        contact.setAddressLine2(rs.getString("address_line2"));
        contact.setState(rs.getString("state"));
        contact.setSuburb(rs.getString("suburb"));
        contact.setPostcode(rs.getString("postcode"));
        contact.setCountry(rs.getString("country"));
        contact.setLatitude(rs.getString("latitude"));
        contact.setLongitude(rs.getString("longitude"));

        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            contact.setCreatedDate(createdDate.toLocalDateTime());
        }

        contact.setCreatedBy(rs.getString("created_by"));

        Timestamp updatedDate = rs.getTimestamp("updated_date");
        if (updatedDate != null) {
            contact.setUpdatedDate(updatedDate.toLocalDateTime());
        }

        contact.setUpdatedBy(rs.getString("updated_by"));

        return contact;
    };

    @Override
    public Contact save(Contact contact, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] saveContact: name={}, email={}",
                requestId, contact.getName(), contact.getEmail());

        try {
            String sql = ContactQueryUtil.insertContactQuery();

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, contact.getName());
                ps.setString(2, contact.getEmail());
                ps.setString(3, contact.getPhone());
                ps.setString(4, contact.getAddressLine1());
                ps.setString(5, contact.getAddressLine2());
                ps.setString(6, contact.getState());
                ps.setString(7, contact.getSuburb());
                ps.setString(8, contact.getPostcode());
                ps.setString(9, contact.getCountry());
                ps.setString(10, contact.getLatitude());
                ps.setString(11, contact.getLongitude());
                ps.setTimestamp(12, Timestamp.valueOf(contact.getCreatedDate()));
                ps.setString(13, contact.getCreatedBy());
                ps.setTimestamp(14, Timestamp.valueOf(contact.getUpdatedDate()));
                ps.setString(15, contact.getUpdatedBy());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                contact.setId(keyHolder.getKey().longValue());
            }

            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] saveContact: contactId={} | timeTaken={}",
                    requestId, contact.getId(), CommonUtils.getExecutionTime(startTime));

            return contact;

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] saveContact: Ex={}|Trace",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to save contact", e);
        }
    }
}