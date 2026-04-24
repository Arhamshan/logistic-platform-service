package com.logistic.platform.repository;

import com.logistic.common.entity.Contact;

public interface ContactRepository {

    Contact save(Contact contact, String requestId);

}
