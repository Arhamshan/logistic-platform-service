package com.logistic.platform.service.impl;

import com.logistic.common.entity.Contact;
import com.logistic.platform.repository.ContactRepository;
import com.logistic.platform.service.ContactService;
import com.logistic.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private ContactRepository contactRepository;

    @Override
    @Transactional
    public Contact createContact(Contact contact, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createContact: name={}, email={}",
                requestId, contact.getName(), contact.getEmail());

        try {
            // Set audit fields
            LocalDateTime now = LocalDateTime.now();
            contact.setCreatedDate(now);
            contact.setUpdatedDate(now);

            if (contact.getCreatedBy() == null) {
                contact.setCreatedBy("system");
            }
            if (contact.getUpdatedBy() == null) {
                contact.setUpdatedBy("system");
            }

            Contact savedContact = contactRepository.save(contact, requestId);

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createContact: contactId={} | timeTaken={}",
                    requestId, savedContact.getId(), CommonUtils.getExecutionTime(startTime));

            return savedContact;

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createContact: Ex={}",
                    requestId, e.getMessage());
            throw e;
        }
    }

}