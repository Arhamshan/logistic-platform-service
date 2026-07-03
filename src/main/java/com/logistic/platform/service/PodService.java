package com.logistic.platform.service;

import com.logistic.common.entity.Pod;

public interface PodService {
    Boolean savePod(String consignmentId, String itemId, Pod pod, String image, String requestId);

    Pod getPodByItemId(Long consItemId, String requestId);
}