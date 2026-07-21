package com.logistic.platform.service;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;

public interface DeliveryAssignmentService {

    Long saveDeliveryAssignment(DeliveryAssignment assignment, String requestId);

    DeliveryAssignmentSummaryDto getSummary(String requestId);
}