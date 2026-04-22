package com.logistic.platform.service;

import com.logistic.common.entity.User;

public interface UserService {

    Boolean createUser(User user, String requestId);
}
