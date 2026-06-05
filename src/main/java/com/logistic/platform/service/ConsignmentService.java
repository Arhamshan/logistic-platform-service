package com.logistic.platform.service;

import com.logistic.common.entity.Consignment;
import com.logistic.platform.vo.ItemProcessResultVo;
import com.logistic.platform.vo.SummaryVo;
import com.logistic.platform.vo.TrackingConsignmentVo;

import java.util.List;

public interface ConsignmentService {

    // Save consignment
    List<ItemProcessResultVo> save(Consignment consignment, String requestId);

    void updateStatus(Consignment consignment, String requestId);

    Boolean existsByConsignmentId(String consignmentId, String requestId);

    TrackingConsignmentVo getByConsignmentId(String consignmentId, String requestId);

    SummaryVo getSummary(String requestId);

    List<Consignment> getAllConsignments(int pageNumber, int pageSize, String sortBy, String sortDir, String requestId);
}