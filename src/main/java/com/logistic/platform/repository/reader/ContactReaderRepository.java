package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Contact;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.ContactRepository;
import com.logistic.platform.util.ContactQueryUtil;
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
public class ContactReaderRepository implements ContactRepository {

    private static final Logger LOGGER = LogManager.getLogger(ContactReaderRepository.class);

    @Qualifier("reader")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Contact findById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findById: id={}",
                requestId, id);

        Contact contact = null;

        try {

            List<Contact> contacts = this.jdbcTemplate.query(ContactQueryUtil.findContactByIdQuery(), new RowMapper<Contact>() {
                @Override
                public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Contact cnt = new Contact();
                    cnt.setId(rs.getLong("id"));
                    cnt.setName(rs.getString("name"));
                    cnt.setEmail(rs.getString("email"));
                    cnt.setPhone(rs.getString("phone"));
                    cnt.setAddressLine1(rs.getString("address_line1"));
                    cnt.setAddressLine2(rs.getString("address_line2"));
                    cnt.setState(rs.getString("state"));
                    cnt.setSuburb(rs.getString("suburb"));
                    cnt.setPostcode(rs.getString("postcode"));
                    cnt.setCountry(rs.getString("country"));

                    return cnt;
                }
            }, id);

            if (contacts != null && !contacts.isEmpty()) {
                contact = contacts.get(0);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to find user. ", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findById: contact={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(contact), CommonUtils.getExecutionTime(startTime));
        }

        return contact;
    }

}