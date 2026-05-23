package com.logistic.platform.service;

import com.logistic.platform.dto.pod.PodRequestDto;

public interface PodService {
    Boolean savePod(String consignmentId, String itemId, PodRequestDto requestDto, String requestId);
}