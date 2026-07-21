package com.logistic.platform.repository;

import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;

public interface DeliveryAssignmentRepository {

    public default Long save(DeliveryAssignment assignment, String requestId){
        return null;
    }

    public default DeliveryAssignmentSummaryDto findSummary(String requestId){
        return null;
    }
}
