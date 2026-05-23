package com.logistic.platform.repository;

import com.logistic.common.entity.Pod;

public interface PodRepository {
    Long save(Pod pod, String requestId);
}