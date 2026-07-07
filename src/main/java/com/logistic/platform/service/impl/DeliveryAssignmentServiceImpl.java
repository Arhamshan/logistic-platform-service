package com.logistic.platform.service.impl;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.DeliveryAssignmentWriterRepository;
import com.logistic.platform.service.DeliveryAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeliveryAssignmentServiceImpl implements DeliveryAssignmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryAssignmentServiceImpl.class);

    private final DeliveryAssignmentWriterRepository writerRepository;

    public DeliveryAssignmentServiceImpl(DeliveryAssignmentWriterRepository writerRepository) {
        this.writerRepository = writerRepository;
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
}