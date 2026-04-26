package com.logistic.platform.service;

import com.logistic.common.entity.Contact;

public interface ContactService {

    // Create new contact
    Contact createContact(Contact contact, String requestId);

}