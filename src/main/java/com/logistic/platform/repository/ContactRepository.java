package com.logistic.platform.repository;

import com.logistic.common.entity.Contact;

public interface ContactRepository {

    public default Contact save(Contact contact, String requestId) {
        return null;
    }

    public default Contact findById(Long id, String requestId) {
        return null;
    }
}
