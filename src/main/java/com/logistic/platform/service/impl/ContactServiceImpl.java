package com.logistic.platform.service.impl;

import com.logistic.common.entity.Contact;
import com.logistic.platform.repository.reader.ContactReaderRepository;
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

    private final ContactWriterRepository writerRepository;
    private final ContactReaderRepository readerRepository;

    public ContactServiceImpl(ContactWriterRepository writerRepository,
                              ContactReaderRepository readerRepository) {
        this.writerRepository = writerRepository;
        this.readerRepository = readerRepository;
    }

    @Override
    @Transactional
    public Contact createContact(Contact contact, String requestId, String username) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createContact: contact={}", requestId, CommonUtils.convertToString(contact));

        Contact savedContact = null;

        try {
            // Set audit fields
            LocalDateTime now = LocalDateTime.now();
            contact.setCreatedDate(now);
            contact.setUpdatedDate(now);

            contact.setCreatedBy(username);
            contact.setUpdatedBy(username);

            savedContact = writerRepository.save(contact, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createContact: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createContact: contactId={}|timeTaken={}",
                requestId, savedContact.getId(), CommonUtils.getExecutionTime(startTime));

        return savedContact;
    }

    @Override
    public Contact getById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getById: id={}", requestId, id);

        Contact contact = readerRepository.findById(id, requestId);

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getById: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return contact;
    }

    @Override
    @Transactional
    public Contact updateContact(Long contactId, Contact contact, String requestId, String username) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateContact: contactId={}",
                requestId, contactId);

        Contact updatedContact = null;

        try {
            contact.setId(contactId);
            contact.setUpdatedDate(LocalDateTime.now());
            contact.setUpdatedBy(username);

            updatedContact = writerRepository.update(contact, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateContact: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateContact: contactId={}|timeTaken={}",
                requestId, contactId, CommonUtils.getExecutionTime(startTime));

        return updatedContact;
    }
}