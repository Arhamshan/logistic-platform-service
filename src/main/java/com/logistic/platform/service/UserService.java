package com.logistic.platform.service;

import com.logistic.common.entity.User;

import java.util.List;

public interface UserService {

    Boolean createUser(User user, String requestId, String username);

    User getByUsername(String username, String requestId);

    List<User> getUsersByType(String type, String requestId);
}
