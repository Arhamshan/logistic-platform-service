package com.logistic.platform.service;

import com.logistic.common.entity.Consignment;
import com.logistic.platform.vo.ItemProcessResultVo;

import java.util.List;

public interface ConsignmentService {

    // Save consignment
    List<ItemProcessResultVo> save(Consignment consignment, String requestId);

    void updateConsignmentStatus(Consignment consignment, String requestId);

    Consignment getByConsignmentId(String consignmentId, String requestId);

}