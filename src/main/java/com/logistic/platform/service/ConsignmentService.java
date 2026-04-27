package com.logistic.platform.service;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Event;
import com.logistic.platform.vo.ItemProcessResult;

import java.util.List;

public interface ConsignmentService {

    // Save consignment
    List<ItemProcessResult> save(Consignment consignment, String locationCode, String requestId);

}