package com.logistic.platform.repository.reader;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Contact;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.GetConsignmentResponseDto;
import com.logistic.platform.dto.item.ConsignmentItemResponseDto;
import com.logistic.platform.repository.ConsignmentRepository;
import com.logistic.platform.util.ConsignmentQueryUtil;
import com.logistic.platform.vo.ConsignmentItemVo;
import com.logistic.platform.vo.ConsignmentVo;
import com.logistic.platform.vo.SummaryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("consignmentReaderRepository")
public class ConsignmentReaderRepository implements ConsignmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentReaderRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ConsignmentReaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Consignment> findByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        List<Consignment> result = null;

        try {
            String sql = ConsignmentQueryUtil.findByConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{consignmentId},
                    new RowMapper<Consignment>() {
                        @Override
                        public Consignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Consignment consignment = new Consignment();
                            consignment.setId(rs.getLong("id"));
                            consignment.setConsignmentId(rs.getString("consignment_id"));
                            consignment.setStatus(ConsignmentStatus.valueOf(rs.getString("status")));
                            return consignment;
                        }
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw new RuntimeException("Failed to fetch consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findByConsignmentId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }

    public Optional<Consignment> findTrackingByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        List<Consignment> result = null;

        try {
            String sql = ConsignmentQueryUtil.findByConsignmentIdQuery();

            result = jdbcTemplate.query(sql, new Object[]{consignmentId},
                    (rs, rowNum) -> {
                        Consignment consignment = new Consignment();
                        consignment.setConsignmentId(rs.getString("consignment_id"));
                        consignment.setStatus(ConsignmentStatus.valueOf(rs.getString("status")));
                        return consignment;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch consignment tracking", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findTrackingByConsignmentId: found={}|timeTaken={}",
                    requestId,
                    result != null && !result.isEmpty(),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result.stream().findFirst();
    }

    public SummaryVo findSummary(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findSummary", requestId);

        SummaryVo result = null;

        try {
            String sql = ConsignmentQueryUtil.findSummaryQuery();

            List<SummaryVo> rows = jdbcTemplate.query(sql,
                    (rs, rowNum) -> {
                        SummaryVo vo = new SummaryVo();
                        vo.setTotalConsignments(rs.getLong("total_consignments"));
                        vo.setBooked(rs.getLong("booked"));
                        vo.setPickedUp(rs.getLong("picked_up"));
                        vo.setInTransit(rs.getLong("in_transit"));
                        vo.setOutForDelivery(rs.getLong("out_for_delivery"));
                        vo.setDelivered(rs.getLong("delivered"));
                        vo.setTodayBookings(rs.getLong("today_bookings"));
                        return vo;
                    });

            if (rows != null && !rows.isEmpty()) {
                result = rows.get(0);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch summary", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findSummary: result={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(result), CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    public List<Consignment> findAll(int pageNumber, int pageSize, String sortBy, String sortDir, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findAll : pageNumber={}|pageSize={}|sortBy={}|sortDir={}",
                requestId, pageNumber, pageSize, sortBy, sortDir);

        List<Consignment> consignments = null;

        try {
            int offSet = pageNumber * pageSize;

            String sql = ConsignmentQueryUtil.findAllQuery(sortBy, sortDir);

            consignments = jdbcTemplate.query(sql, new Object[]{pageSize, offSet},
                    (rs, rowNum) -> {
                        Consignment consignment = new Consignment();
                        consignment.setId(rs.getLong("id"));
                        consignment.setConsignmentId(rs.getString("consignment_id"));
                        consignment.setStatus(ConsignmentStatus.valueOf(rs.getString("status")));
                        consignment.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());

                        Contact senderContact = new Contact();
                        senderContact.setId(rs.getLong("sender_contact_id"));


                        Contact destinationContact = new Contact();
                        destinationContact.setId(rs.getLong("destination_contact_id"));

                        consignment.setSenderContact(senderContact);
                        consignment.setDestinationContact(destinationContact);

                        return consignment;
                    });

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findAll: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch summary", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findAll: result={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(consignments), CommonUtils.getExecutionTime(startTime));
        }

        return consignments;
    }

    public Optional<ConsignmentVo> findById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REPOSITORY-LAYER] [RequestId={}] findById: id={}", requestId, id);

        ConsignmentVo  result = null;

        try {
            String sql = ConsignmentQueryUtil.findByIdQuery();

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

            if (rows != null && !rows.isEmpty()) {
                result = new ConsignmentVo();
                result.setConsignmentId((String) rows.get(0).get("consignment_id"));
                result.setStatus((String) rows.get(0).get("status"));

                result.setSenderContactId(rows.get(0).get("sender_contact_id") != null
                        ? ((Number) rows.get(0).get("sender_contact_id")).longValue()
                        : null);
                result.setDestinationContactId(rows.get(0).get("destination_contact_id") != null
                        ? ((Number) rows.get(0).get("destination_contact_id")).longValue()
                        : null);

                List<ConsignmentItemVo> items = new ArrayList<>();
                for (Map<String, Object> row : rows) {
                    if (row.get("item_pk") != null) {
                        items.add(new ConsignmentItemVo(
                                (String) row.get("item_id"),
                                (String) row.get("item_status"),
                                (String) row.get("current_location_code")
                        ));
                    }
                }
                result.setItems(items);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [REPOSITORY-LAYER] [RequestId={}] findById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw new RuntimeException("Failed to fetch consignment", e);

        } finally {
            LOGGER.info("END [REPOSITORY-LAYER] [RequestId={}] findById: found={}|timeTaken={}",
                    requestId, result != null, CommonUtils.getExecutionTime(startTime));
        }

        return result != null ? Optional.of(result) : Optional.empty();
    }
}
