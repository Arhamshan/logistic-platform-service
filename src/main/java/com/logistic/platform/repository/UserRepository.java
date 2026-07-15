package com.logistic.platform.repository;

import com.logistic.common.entity.User;

public interface UserRepository {

    public default boolean save(User user, String requestId) {
        return Boolean.FALSE;
    }

    public default User findByUsername(String username, String requestId) {
        return null;
    }
}
