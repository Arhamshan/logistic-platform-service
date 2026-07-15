package com.logistic.platform.repository;

import com.logistic.common.entity.DeliveryAssignment;

public interface DeliveryAssignmentRepository {

    public default Long save(DeliveryAssignment assignment, String requestId){
        return null;
    }
}
