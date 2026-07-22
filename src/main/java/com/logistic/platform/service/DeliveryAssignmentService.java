package com.logistic.platform.service;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;
import com.logistic.platform.vo.DeliveryAssignmentSummaryVo;

public interface DeliveryAssignmentService {

    Long saveDeliveryAssignment(DeliveryAssignment assignment, String requestId);

    DeliveryAssignmentSummaryVo getSummary(String requestId);

    Boolean updateDeliveryAssignment(Long driverId, Long consItemId, String status, String requestId);
}