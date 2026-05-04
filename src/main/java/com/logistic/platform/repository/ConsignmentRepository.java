package com.logistic.platform.repository;


import com.logistic.common.entity.Consignment;

import java.util.Optional;

public interface ConsignmentRepository {

    public default Long save(Consignment consignment, String requestId){
        return null;
    };

    public default Optional<Consignment> findByConsignmentId(String consignmentId, String requestId){
        return Optional.empty();
    };

    public default Consignment update(Consignment consignment, String requestId){
        return null;
    };

}