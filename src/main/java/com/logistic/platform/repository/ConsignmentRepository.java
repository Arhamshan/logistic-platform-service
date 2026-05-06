package com.logistic.platform.repository;


import com.logistic.common.entity.Consignment;

import java.util.Optional;

public interface ConsignmentRepository {

    public default Long save(Consignment consignment, String requestId){
        return null;
    };

    public default Consignment update(Consignment consignment, String requestId){
        return null;
    };

}