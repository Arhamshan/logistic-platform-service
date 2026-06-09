package com.logistic.platform.repository;


import com.logistic.common.entity.Consignment;
import com.logistic.platform.dto.consignment.GetConsignmentResponseDto;
import com.logistic.platform.vo.ConsignmentVo;
import com.logistic.platform.vo.SummaryVo;
import com.logistic.platform.vo.TrackingConsignmentVo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ConsignmentRepository {

    public default Long save(Consignment consignment, String requestId){
        return null;
    };

    public default Consignment update(Consignment consignment, String requestId){
        return null;
    };

    public default Optional<Consignment> findTrackingByConsignmentId(String consignmentId, String requestId) {
        return null;
    };

    public default SummaryVo findSummary(String requestId) {
        return null;
    }

    public default List<Consignment> findAll(int pageNumber, int pageSize, String sortBy, String sortDir, String requestId) {
        return Collections.emptyList();
    }

    public default Boolean deleteById(Long id, String requestId) {
        return false;
    };

    public default Optional<ConsignmentVo> findById(Long id, String requestId){
        return Optional.empty();
    }


    }