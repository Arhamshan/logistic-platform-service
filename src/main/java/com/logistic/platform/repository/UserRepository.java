package com.logistic.platform.repository;

import com.logistic.common.entity.User;

public interface UserRepository {

    boolean save(User user, String requestId);
}
