package com.logistic.platform.service;

import com.logistic.common.entity.DeliveryAssignment;

public interface DeliveryAssignmentService {

    Long saveDeliveryAssignment(DeliveryAssignment assignment, String requestId);
}