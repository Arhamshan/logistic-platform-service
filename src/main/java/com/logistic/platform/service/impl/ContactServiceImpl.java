package com.logistic.platform.service.impl;

import com.logistic.common.entity.Contact;
import com.logistic.platform.repository.writer.ContactWriterRepository;
import com.logistic.platform.service.ContactService;
import com.logistic.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactWriterRepository contactWriterRepository;

    public ContactServiceImpl(ContactWriterRepository contactWriterRepository) {
        this.contactWriterRepository = contactWriterRepository;
    }

    @Override
    @Transactional
    public Contact createContact(Contact contact, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createContact: contact={}", requestId, CommonUtils.convertToString(contact));

        Contact savedContact = null;

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

            savedContact = contactWriterRepository.save(contact, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createContact: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createContact: contactId={}|timeTaken={}",
                requestId, savedContact.getId(), CommonUtils.getExecutionTime(startTime));

        return savedContact;
    }

}