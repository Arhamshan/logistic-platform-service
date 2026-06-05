package com.logistic.platform.service;

import com.logistic.common.entity.Contact;

public interface ContactService {

    /*
    * Create new contact
    * */
    Contact createContact(Contact contact, String requestId);

    /*
    * Get contact by id
    * */
    Contact getById(Long id, String requestId);

}