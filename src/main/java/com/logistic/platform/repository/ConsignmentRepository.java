package com.logistic.platform.repository;


import com.logistic.common.entity.Consignment;

public interface ConsignmentRepository {

    Long save(Consignment consignment, String requestId);

}