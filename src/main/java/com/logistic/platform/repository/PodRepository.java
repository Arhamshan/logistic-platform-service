package com.logistic.platform.repository;

import com.logistic.common.entity.Pod;
import com.logistic.platform.dto.pod.PodResponseDto;

public interface PodRepository {
    public default Long save(Pod pod, String requestId){
        return null;
    };

    public default PodResponseDto findByConsItemId(Long consItemId, String requestId){
        return null;
    };
}