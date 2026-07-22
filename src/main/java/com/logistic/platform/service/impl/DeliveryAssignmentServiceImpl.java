package com.logistic.platform.service.impl;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.common.entity.Event;
import com.logistic.common.enums.EventType;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;
import com.logistic.platform.repository.reader.DeliveryAssignmentReaderRepository;
import com.logistic.platform.repository.writer.DeliveryAssignmentWriterRepository;
import com.logistic.platform.service.DeliveryAssignmentService;
import com.logistic.platform.service.EventService;
import com.logistic.platform.vo.DeliveryAssignmentSummaryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeliveryAssignmentServiceImpl implements DeliveryAssignmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryAssignmentServiceImpl.class);

    private final DeliveryAssignmentWriterRepository writerRepository;
    private final DeliveryAssignmentReaderRepository readerRepository;
    private final EventService eventService;

    public DeliveryAssignmentServiceImpl(DeliveryAssignmentWriterRepository writerRepository,
                                         DeliveryAssignmentReaderRepository readerRepository,
                                         EventService eventService) {
        this.writerRepository = writerRepository;
        this.readerRepository = readerRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public Long saveDeliveryAssignment(DeliveryAssignment assignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] saveDeliveryAssignment: assignment={}",
                requestId, CommonUtils.convertToString(assignment));

        Long generatedId = null;

        try {
            // Set audit timestamps if not already set
            if (assignment.getCreatedDate() == null) {
                assignment.setCreatedDate(LocalDateTime.now());
            }
            if (assignment.getUpdatedDate() == null) {
                assignment.setUpdatedDate(LocalDateTime.now());
            }
            if (assignment.getAssignedDatetime() == null) {
                assignment.setAssignedDatetime(LocalDateTime.now());
            }

            generatedId = writerRepository.save(assignment, requestId);

            if (generatedId == null) {
                throw new RuntimeException("DeliveryAssignment save returned no generated ID");
            }

            Event event = new Event();
            event.setItem(assignment.getItem());
            event.setEventType(EventType.DRIVER_ASSIGNED);
            event.setDescription("Item assigned to driver " + assignment.getDriver().getId());
            event.setCreatedBy(assignment.getAssignedBy());
            event.setCreatedDate(LocalDateTime.now());

            eventService.saveEvent(event, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] saveDeliveryAssignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] saveDeliveryAssignment: generatedId={}|timeTaken={}",
                    requestId, generatedId, CommonUtils.getExecutionTime(startTime));
        }

        return generatedId;
    }

    @Override
    public DeliveryAssignmentSummaryVo getSummary(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getSummary", requestId);

        DeliveryAssignmentSummaryVo result = null;

        try {
            result = readerRepository.findSummary(requestId);

            if (result == null) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] getSummary: No summary data returned",
                        requestId);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getSummary: result={}|timeTaken={}",
                    requestId,
                    CommonUtils.convertToString(result),
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    @Override
    public Boolean updateDeliveryAssignment(Long driverId, Long consItemId,
                                            String status, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateDeliveryAssignment: driverId={}|consItemId={}|status={}",
                requestId, driverId, consItemId, status);

        Boolean result = Boolean.FALSE;

        try {
            if (driverId == null || consItemId == null || status == null || status.isBlank()) {
                throw new IllegalArgumentException(
                        "driverId, consItemId and status are required");
            }

            result = writerRepository.updateStatus(driverId, consItemId, status, requestId);

            if (Boolean.FALSE.equals(result)) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] updateDeliveryAssignment: " +
                                "No record found for driverId={}|consItemId={}",
                        requestId, driverId, consItemId);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateDeliveryAssignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateDeliveryAssignment: result={}|timeTaken={}",
                    requestId, result, CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}