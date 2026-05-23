package com.logistic.platform.repository;


import com.logistic.common.entity.Consignment;
import com.logistic.platform.vo.SummaryVo;
import com.logistic.platform.vo.TrackingConsignmentVo;

import java.util.Optional;

public interface ConsignmentRepository {

    public default Long save(Consignment consignment, String requestId){
        return null;
    };

    public default Consignment update(Consignment consignment, String requestId){
        return null;
    };

    public default Optional<Consignment> findTrackingByConsignmentId(String consignmentId, String requestId){
        return null;
    };

    public default SummaryVo findSummary(String requestId){
        return null;
    }

}